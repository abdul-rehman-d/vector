package expo.modules.vectornavigation

import android.app.Notification

object NavigationNotificationParser {
  private val maneuverRegex = Regex(
    """\b(turn|keep|bear|take|continue|head|merge|exit|u[- ]?turn|roundabout|destination|arrive|rerouting)\b""",
    RegexOption.IGNORE_CASE,
  )
  private val distanceRegex = Regex(
    """\b\d+(?:[.,]\d+)?\s*(?:m|km|ft|mi|yd)\b""",
    RegexOption.IGNORE_CASE,
  )
  private val durationRegex = Regex(
    """\b(?:\d+\s*(?:h|hr|hrs|hour|hours)\s*)?\d+\s*(?:min|mins|minute|minutes)\b""",
    RegexOption.IGNORE_CASE,
  )
  private val roadSeparatorRegex = Regex(
    """\s+(?:on|onto|toward|towards)\s+""",
    RegexOption.IGNORE_CASE,
  )
  private val arrivalTimeRegex = Regex(
    """^arrive\s+(?:at\s+)?\d{1,2}:\d{2}(?:\s*[ap]\.?m\.?)?$""",
    RegexOption.IGNORE_CASE,
  )
  private val leadingDistanceRegex = Regex(
    """^(?:in\s+)?\d+(?:[.,]\d+)?\s*(?:m|km|ft|mi|yd)\s*[•·|,:—-]*\s*""",
    RegexOption.IGNORE_CASE,
  )

  fun parse(notification: Notification): NavigationStateData? {
    val lines = notificationText(notification)
    val channelLooksLikeNavigation = notification.channelId
      ?.contains("navigation", ignoreCase = true) == true
    val categoryLooksLikeNavigation = notification.category == "navigation"
    val isOngoing = notification.flags and Notification.FLAG_ONGOING_EVENT != 0

    return parseText(
      lines = lines,
      channelLooksLikeNavigation = channelLooksLikeNavigation,
      categoryLooksLikeNavigation = categoryLooksLikeNavigation,
      isOngoing = isOngoing,
    )
  }

  internal fun parseText(
    lines: List<String>,
    channelLooksLikeNavigation: Boolean = true,
    categoryLooksLikeNavigation: Boolean = true,
    isOngoing: Boolean = true,
  ): NavigationStateData? {
    if (lines.isEmpty()) return null

    val combined = lines.joinToString(" • ")
    val durationStatusLine = lines.firstOrNull { durationRegex.containsMatchIn(it) }
    val arrivalStatusLine = lines.firstOrNull { arrivalTimeRegex.matches(it) }
    val statusLines = setOfNotNull(durationStatusLine, arrivalStatusLine)
    val maneuverLine = lines.firstOrNull {
      it !in statusLines && maneuverRegex.containsMatchIn(it)
    }

    if (!channelLooksLikeNavigation && !categoryLooksLikeNavigation &&
      !(isOngoing && maneuverLine != null)
    ) {
      return null
    }

    val distance = maneuverLine?.let(distanceRegex::find)?.value
      ?: lines
        .asSequence()
        .filter { it !in statusLines }
        .mapNotNull { distanceRegex.find(it)?.value }
        .firstOrNull()
    val eta = durationStatusLine?.let(durationRegex::find)?.value
      ?: arrivalStatusLine
    val remainingDistance = durationStatusLine
      ?.let { distanceRegex.findAll(it).lastOrNull()?.value }
      ?.takeUnless { it.equals(distance, ignoreCase = true) }
      ?: distanceRegex.findAll(combined).map { it.value }.lastOrNull()
        ?.takeUnless { it.equals(distance, ignoreCase = true) }

    val cleanedManeuver = maneuverLine
      ?.replace(leadingDistanceRegex, "")
      ?.trim(' ', '•', '·', '-', '—', ',')
      ?.takeIf(String::isNotBlank)
    val roadMatch = cleanedManeuver?.let { roadSeparatorRegex.find(it) }
    val instruction = when {
      cleanedManeuver == null -> null
      roadMatch == null -> cleanedManeuver
      else -> cleanedManeuver.substring(0, roadMatch.range.first).trim()
    }
    val roadFromInstruction = roadMatch?.let {
      cleanedManeuver
        ?.substring(it.range.last + 1)
        ?.substringBefore(" • ")
        ?.trim()
        ?.takeIf(String::isNotBlank)
    }
    val road = roadFromInstruction ?: lines.firstOrNull { line ->
      line != maneuverLine && line !in statusLines &&
        !distanceRegex.matches(line) &&
        !line.contains("navigation", ignoreCase = true) &&
        !line.contains("driving directions", ignoreCase = true)
    }

    return NavigationStateData(
      instruction = instruction,
      distance = distance,
      road = road,
      eta = eta,
      remainingDistance = remainingDistance,
      isNavigating = true,
    )
  }

  fun notificationText(notification: Notification): List<String> {
    val extras = notification.extras
    val values = buildList {
      add(extras.getCharSequence(Notification.EXTRA_TITLE)?.toString())
      add(extras.getCharSequence(Notification.EXTRA_TEXT)?.toString())
      add(extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString())
      add(extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString())
      add(extras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString())
      add(extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString())
      add(extras.getCharSequence("android.shortCriticalText")?.toString())
      extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
        ?.forEach { add(it?.toString()) }
      add(notification.tickerText?.toString())
    }

    return values
      .filterNotNull()
      .flatMap { it.lines() }
      .map { it.trim() }
      .filter(String::isNotBlank)
      .distinct()
  }
}
