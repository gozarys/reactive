export const Layout = ({children}) => 
{
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const drawerOpen = useSelector(state => state.layout.drawerOpen);
    const doToggleDrawer = () => dispatch(toggleDrawer());
    return 
    (
        <Box sx={{display: 'flex'}}>
            <TopBar
                goHome={() => navigate('/')}
                newTask={doToggleDrawer} drawerOpen={drawerOpen}
            />
            <MainDrawer toggleDrawer={doToggleDrawer} drawerOpen={drawerOpen}
            />
            <Box sx={{flex: 1}}>
                <Toolbar />
                <Box component='main'>
                    {children}
                </Box>
            </Box>
    );
};