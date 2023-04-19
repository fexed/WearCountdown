# WearCountdown
Small countdown app for Wear OS. It is built experimenting with few things:

- **Navigation in Jetpack Compose**: the various dialogs used to set the countdown are navigated through a `SwipeDismissableNavController`. A colleague recently explained how it works to me (thanks Donatella!) and I wanted to try it out, really useful!
- **Wear OS Layout**: yeah I used Compose once again, but designing for such a small screen is not an easy task!
- **Wear OS Tiles**: the Tiles that you can set in the home screen of a Wear OS device are set through a `TileService`, and in my case I used a `SuspendingTileService` provided by the Horologist library
- **Wear OS Complications**: Complications that you can add to some watchfaces, implemented in three variants with `SuspendingComplicationDataSourceService`
