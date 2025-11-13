package com.example;

import java.util.Objects;
import java.util.Optional;

// no direct HibernateException import required for this mapper
import org.hibernate.ObjectNotFoundException;
import org.hibernate.StaleObjectStateException;

import io.vertx.pgclient.PgException;
import java.lang.reflect.Method;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RestExceptionHandler implements ExceptionMapper<Throwable>
{

    private static final String PG_UNIQUE_VIOLATION_ERROR = "23505";
    
    
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> Optional<T> getExceptionInChain(Throwable throwable, Class<T> exceptionClass) {
        while (throwable != null) {
            if (exceptionClass.isInstance(throwable)) {
                return Optional.of((T) throwable);
            }
            throwable = throwable.getCause();
        }
        return Optional.empty();
    }
    
    private static boolean hasExceptionInChain(Throwable throwable, Class<? extends Throwable> exceptionClass)
    {    
        return getExceptionInChain(throwable, exceptionClass).isPresent();
    }

    private static Optional<String> extractSqlStateFrom(Throwable t) {
        Optional<Object> ex = getExceptionInChain(t, PgException.class).map(e -> (Object) e);
        if (ex.isPresent()) {
            Object e = ex.get();
            String[] methodNames = new String[]{"getSqlState", "getSQLState", "getCode"};
            try {
                for (String mName : methodNames) {
                    Method m = e.getClass().getMethod(mName);
                    Object res = m.invoke(e);
                    if (res != null) {
                        return Optional.of(res.toString());
                    }
                }
            } catch (Exception ignored) {
                // ignore reflection issues and fall back to message scan
            }
        }
        // fallback: look for SQL state code in any message in the chain
        Throwable cur = t;
        while (cur != null) {
            String m = cur.getMessage();
            if (m != null && m.contains(PG_UNIQUE_VIOLATION_ERROR)) {
                return Optional.of(PG_UNIQUE_VIOLATION_ERROR);
            }
            cur = cur.getCause();
        }
        return Optional.empty();
    }

    private static boolean hasPostgreErrorCode(
        Throwable throwable, String code)
    {
        return extractSqlStateFrom(throwable).filter(s -> Objects.equals(s, code)).isPresent();
    }

    @Override
    public Response toResponse(Throwable exception)
    {
        String msg = exception == null ? "" : exception.getMessage();
        if (hasExceptionInChain(exception, ObjectNotFoundException.class))
        {
            return Response.status(Response.Status.NOT_FOUND)
            .entity(msg).build();
        }
        if (hasExceptionInChain(exception,
         StaleObjectStateException.class) || hasPostgreErrorCode(exception,
          PG_UNIQUE_VIOLATION_ERROR))
         {
            return Response.status(Response.Status.CONFLICT).build();
         }
        return Response.status(Response.Status.BAD_REQUEST)
        .entity("\"" + msg + "\"")
        .build();
    }
}
