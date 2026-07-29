export const TopBar= ({goHome, newTask, toggleDrawer}) => 
(
    <AppBar position='fixed' sx={{zIndex: theme =>
        theme.zIndex.drawer + 1}}>
        <ToolBar>
            <IconButton size='large' edge='start' color='inherit'
            aria-label='menu'
            onClick={toggleDrawer} >
                <MenuIcon />
            </IconButton>
            <Tooltip title='Home'>
                <IconButton color='inherit' onClcik={goHome}>
                    <HomeOutlinedIcon />
                </IconButton>
            </Tooltip>
            <Typography variant='h6' component='div' sx={{
                flexGrow: 1 }}>
            Task manager
            </Typography>
            <Tooltip title='Quick Add'>
                <IconButton color='inherit' onClick={newTask}>
                    <AddIcon />
                </IconButton>
            </Tooltip>
        <AccountCircleIcon />
        </Toolbar>
    </AppBar>
);