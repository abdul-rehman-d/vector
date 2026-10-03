package expo.modules.vectornavigation

import java.util.concurrent.CopyOnWriteArraySet

data class NavigationStateData(
  val instruction: String? = null,
  val distance: String? = null,
  val road: String? = null,
  val eta: String? = null,
  val remainingDistance: String? = null,
  val isNavigating: Boolean = false,
) {
  fun toMap(): Map<String, Any?> = mapOf(
    "instruction" to instruction,
    "distance" to distance,
    "road" to road,
    "eta" to eta,
    "remainingDistance" to remainingDistance,
    "isNavigating" to isNavigating,
  )
}

object NavigationStateStore {
  private val listeners = CopyOnWriteArraySet<(NavigationStateData) -> Unit>()

  @Volatile
  var currentState: NavigationStateData = NavigationStateData()
    private set

  fun publish(state: NavigationStateData) {
    currentState = state
    listeners.forEach { it(state) }
  }

  fun addListener(listener: (NavigationStateData) -> Unit) {
    listeners.add(listener)
  }

  fun removeListener(listener: (NavigationStateData) -> Unit) {
    listeners.remove(listener)
  }
}
