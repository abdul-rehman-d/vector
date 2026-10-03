package expo.modules.vectornavigation

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class VectorNavigationModule : Module() {
  private val navigationStateListener: (NavigationStateData) -> Unit = { state ->
    sendEvent("onNavigationStateChanged", state.toMap())
  }

  override fun definition() = ModuleDefinition {
    Name("VectorNavigation")

    Events("onNavigationStateChanged")

    OnCreate {
      NavigationStateStore.addListener(navigationStateListener)
    }

    OnDestroy {
      NavigationStateStore.removeListener(navigationStateListener)
    }

    Function("getCurrentState") {
      NavigationStateStore.currentState.toMap()
    }

    Function("isNotificationAccessEnabled") {
      val context = requireNotNull(appContext.reactContext)
      val listener = ComponentName(context, VectorNotificationListenerService::class.java)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.isNotificationListenerAccessGranted(listener)
      } else {
        Settings.Secure.getString(
          context.contentResolver,
          "enabled_notification_listeners",
        )
          ?.split(':')
          ?.mapNotNull(ComponentName::unflattenFromString)
          ?.any { it == listener } == true
      }
    }

    Function("openNotificationAccessSettings") {
      val context = requireNotNull(appContext.reactContext)
      val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    }
  }
}
