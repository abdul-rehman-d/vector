package expo.modules.vectornavigation

import android.app.Notification
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class VectorNotificationListenerService : NotificationListenerService() {
  private data class ActiveNavigation(
    val state: NavigationStateData,
    val postedAt: Long,
  )

  private val activeNavigations = mutableMapOf<String, ActiveNavigation>()

  override fun onListenerConnected() {
    super.onListenerConnected()
    activeNavigations.clear()
    activeNotifications
      ?.filter { it.packageName == GOOGLE_MAPS_PACKAGE }
      ?.forEach(::handleGoogleMapsNotification)
    publishLatestState()
  }

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    if (sbn?.packageName != GOOGLE_MAPS_PACKAGE) return
    handleGoogleMapsNotification(sbn)
    publishLatestState()
  }

  override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    if (sbn?.packageName != GOOGLE_MAPS_PACKAGE) return
    activeNavigations.remove(sbn.key)
    publishLatestState()
  }

  override fun onListenerDisconnected() {
    activeNavigations.clear()
    NavigationStateStore.publish(NavigationStateData())
    super.onListenerDisconnected()
  }

  private fun handleGoogleMapsNotification(sbn: StatusBarNotification) {
    logRawNotification(sbn)
    val parsed = NavigationNotificationParser.parse(sbn.notification)
    if (parsed == null) {
      activeNavigations.remove(sbn.key)
    } else {
      activeNavigations[sbn.key] = ActiveNavigation(parsed, sbn.postTime)
    }
  }

  private fun publishLatestState() {
    val latest = activeNavigations.values.maxByOrNull(ActiveNavigation::postedAt)
    NavigationStateStore.publish(latest?.state ?: NavigationStateData())
  }

  private fun logRawNotification(sbn: StatusBarNotification) {
    val isDebuggable = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    if (!isDebuggable) return

    val notification = sbn.notification
    Log.d(
      LOG_TAG,
      "Google Maps notification: key=${sbn.key}, id=${sbn.id}, " +
        "channel=${notification.channelId}, category=${notification.category}, " +
        "flags=${notification.flags}, ongoing=${sbn.isOngoing}",
    )
    notification.extras.keySet().sorted().forEach { key ->
      Log.d(LOG_TAG, "extra[$key]=${formatExtra(notification.extras, key)}")
    }
  }

  @Suppress("DEPRECATION")
  private fun formatExtra(extras: Bundle, key: String): String = when (val value = extras.get(key)) {
    is Array<*> -> value.joinToString(prefix = "[", postfix = "]")
    is IntArray -> value.joinToString(prefix = "[", postfix = "]")
    is LongArray -> value.joinToString(prefix = "[", postfix = "]")
    else -> value?.toString() ?: "null"
  }

  companion object {
    private const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"
    private const val LOG_TAG = "VectorNavigation"
  }
}
