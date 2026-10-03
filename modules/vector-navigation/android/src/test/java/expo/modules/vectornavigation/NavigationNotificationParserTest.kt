package expo.modules.vectornavigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationNotificationParserTest {
  @Test
  fun parsesTurnWithDistanceRoadAndArrivalTime() {
    val state = parse(
      "400 m · Turn left onto Hassan Square Flyover/Sir Shah Muhammad Suleman Rd",
      "Arrive 10:26 AM",
      "400 m",
    )

    assertEquals("Turn left", state.instruction)
    assertEquals("400 m", state.distance)
    assertEquals("Hassan Square Flyover/Sir Shah Muhammad Suleman Rd", state.road)
    assertEquals("Arrive 10:26 AM", state.eta)
    assertNull(state.remainingDistance)
    assertTrue(state.isNavigating)
  }

  @Test
  fun splitsHeadingInstructionFromRoad() {
    val state = parse(
      "Head northeast on Sehba Akhter Rd",
      "Arrive 10:26 AM",
    )

    assertEquals("Head northeast", state.instruction)
    assertNull(state.distance)
    assertEquals("Sehba Akhter Rd", state.road)
    assertEquals("Arrive 10:26 AM", state.eta)
  }

  @Test
  fun keepsReroutingAsInstructionWithoutInventingRoad() {
    val state = parse(
      "Rerouting...",
      "Arrive 10:26 AM",
    )

    assertEquals("Rerouting...", state.instruction)
    assertNull(state.road)
    assertEquals("Arrive 10:26 AM", state.eta)
  }

  @Test
  fun preservesDurationAndRemainingDistanceSummary() {
    val state = parse(
      "300 m · Turn right onto Shahrah-e-Faisal",
      "18 min • 7.2 km",
    )

    assertEquals("18 min", state.eta)
    assertEquals("7.2 km", state.remainingDistance)
  }

  @Test
  fun rejectsUnrelatedNotification() {
    val state = NavigationNotificationParser.parseText(
      lines = listOf("Turn on your account alerts"),
      channelLooksLikeNavigation = false,
      categoryLooksLikeNavigation = false,
      isOngoing = false,
    )

    assertNull(state)
  }

  private fun parse(vararg lines: String): NavigationStateData =
    requireNotNull(NavigationNotificationParser.parseText(lines.toList()))
}
