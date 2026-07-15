package io.github.riiimc.riiimc_leveling.client

import io.github.riiimc.riiimc_leveling.RiiiMcLeveling
import io.github.riiimc.riiimc_leveling.packet.UpgradeRequestPacketPayload
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.network.PacketDistributor
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt


@EventBusSubscriber(modid = RiiiMcLeveling.MODID, value = [Dist.CLIENT])
object TableMinigameEvent {
    var isVisible: Boolean = false
        private set
    var minigameStarted: Boolean = false
    var crouchReleasedSinceStart: Boolean = false
    var hitsRemaining: Int = 0
    var maxHits: Int = 0
    var arrowPosition: Float = 1f

    // Initialize with placeholder defaults (will be overridden later)
    var arrowSpeed: Float = 0f
    var maxArrowSpeed: Float = 0f
    var speedIncreasePerHit: Float = 0f
    var movingRight: Boolean = true
    var perfectHits: Int = 0
    var goodHits: Int = 0
    var missedHits: Int = 0

    var perfectZoneStart: Int = 45
    var perfectZoneEnd: Int = 55
    var goodZoneStart: Int = 35
    var goodZoneEnd: Int = 65
    var zoneShrinkFactor: Float = 0.95f
    var zoneShiftAmount: Float = 15.0f
    var perfectZoneSize: Float = (perfectZoneEnd - perfectZoneStart).toFloat()
    var minPerfectSize: Float = 4f
    var skillLevel: Int = 0

    private const val TICKS_PER_PRINT = 1
    private var tickAccumulator = 0
    private var movingDown = false

    // ===============================
    // Popup system
    // ===============================
    val popups: MutableList<Popup> = ArrayList<Popup>()
    private const val POPUP_DURATION_MS = 1500f
    private var lastPerfect = 0
    private var lastGood = 0
    private var lastMiss = 0

    fun ensureInitialized() {
        // Only run if defaults haven't been set yet
        if (arrowSpeed == 0f) {
            setupForQuality("none") // or whichever quality you want as baseline
        }
    }

    fun setupForQuality(quality: String) {
        when (quality.lowercase()) {
            "none" -> {
                arrowSpeed = 2.0f
                speedIncreasePerHit = 0.6f
                maxArrowSpeed = 8f
                zoneShrinkFactor = 0.9f
                perfectZoneStart = (100 - 20) / 2
                perfectZoneEnd = (100 + 20) / 2
                goodZoneStart = Math.max((100 - 20 * 3) / 2, 1)
                goodZoneEnd = Math.min((100 + 20 * 3) / 2, 100)
                minPerfectSize = 8f
                perfectZoneSize = (perfectZoneEnd - perfectZoneStart).toFloat()
            }
            /*
            "master" -> {
                arrowSpeed = ServerConfig.MASTER_ARROW_SPEED.get().floatValue()
                speedIncreasePerHit = ServerConfig.MASTER_ARROW_SPEED_INCREASE.get().floatValue()
                maxArrowSpeed = ServerConfig.MASTER_MAX_ARROW_SPEED.get().floatValue()
                zoneShrinkFactor = ServerConfig.MASTER_ZONE_SHRINK_FACTOR.get().floatValue()
                perfectZoneStart = (100 - ServerConfig.MASTER_ZONE_STARTING_SIZE.get()) / 2
                perfectZoneEnd = (100 + ServerConfig.MASTER_ZONE_STARTING_SIZE.get()) / 2
                goodZoneStart = Math.max((100 - ServerConfig.MASTER_ZONE_STARTING_SIZE.get() * 3) / 2, 1)
                goodZoneEnd = Math.min((100 + ServerConfig.MASTER_ZONE_STARTING_SIZE.get() * 3) / 2, 100)
                minPerfectSize = ServerConfig.MASTER_MIN_PERFECT_ZONE.get()
                perfectZoneSize = (perfectZoneEnd - perfectZoneStart).toFloat()
            }

            "perfect" -> {
                arrowSpeed = ServerConfig.PERFECT_ARROW_SPEED.get().floatValue()
                speedIncreasePerHit = ServerConfig.PERFECT_ARROW_SPEED_INCREASE.get().floatValue()
                maxArrowSpeed = ServerConfig.PERFECT_MAX_ARROW_SPEED.get().floatValue()
                zoneShrinkFactor = ServerConfig.PERFECT_ZONE_SHRINK_FACTOR.get().floatValue()
                perfectZoneStart = (100 - ServerConfig.PERFECT_ZONE_STARTING_SIZE.get()) / 2
                perfectZoneEnd = (100 + ServerConfig.PERFECT_ZONE_STARTING_SIZE.get()) / 2
                goodZoneStart = Math.max((100 - ServerConfig.PERFECT_ZONE_STARTING_SIZE.get() * 3) / 2, 1)
                goodZoneEnd = Math.min((100 + ServerConfig.PERFECT_ZONE_STARTING_SIZE.get() * 3) / 2, 100)
                minPerfectSize = ServerConfig.PERFECT_MIN_PERFECT_ZONE.get()
                perfectZoneSize = (perfectZoneEnd - perfectZoneStart).toFloat()
            }

            "expert" -> {
                arrowSpeed = ServerConfig.EXPERT_ARROW_SPEED.get().floatValue()
                speedIncreasePerHit = ServerConfig.EXPERT_ARROW_SPEED_INCREASE.get().floatValue()
                maxArrowSpeed = ServerConfig.EXPERT_MAX_ARROW_SPEED.get().floatValue()
                zoneShrinkFactor = ServerConfig.EXPERT_ZONE_SHRINK_FACTOR.get().floatValue()
                perfectZoneStart = (100 - ServerConfig.EXPERT_ZONE_STARTING_SIZE.get()) / 2
                perfectZoneEnd = (100 + ServerConfig.EXPERT_ZONE_STARTING_SIZE.get()) / 2
                goodZoneStart = Math.max((100 - ServerConfig.EXPERT_ZONE_STARTING_SIZE.get() * 3) / 2, 1)
                goodZoneEnd = Math.min((100 + ServerConfig.EXPERT_ZONE_STARTING_SIZE.get() * 3) / 2, 100)
                minPerfectSize = ServerConfig.EXPERT_MIN_PERFECT_ZONE.get()
                perfectZoneSize = (perfectZoneEnd - perfectZoneStart).toFloat()
            }

            "well" -> {
                arrowSpeed = ServerConfig.WELL_ARROW_SPEED.get().floatValue()
                speedIncreasePerHit = ServerConfig.WELL_ARROW_SPEED_INCREASE.get().floatValue()
                maxArrowSpeed = ServerConfig.WELL_MAX_ARROW_SPEED.get().floatValue()
                zoneShrinkFactor = ServerConfig.WELL_ZONE_SHRINK_FACTOR.get().floatValue()
                perfectZoneStart = (100 - ServerConfig.WELL_ZONE_STARTING_SIZE.get()) / 2
                perfectZoneEnd = (100 + ServerConfig.WELL_ZONE_STARTING_SIZE.get()) / 2
                goodZoneStart = Math.max((100 - ServerConfig.WELL_ZONE_STARTING_SIZE.get() * 3) / 2, 1)
                goodZoneEnd = Math.min((100 + ServerConfig.WELL_ZONE_STARTING_SIZE.get() * 3) / 2, 100)
                minPerfectSize = ServerConfig.WELL_MIN_PERFECT_ZONE.get()
                perfectZoneSize = (perfectZoneEnd - perfectZoneStart).toFloat()
            }

            else -> {
                // poor
                arrowSpeed = ServerConfig.POOR_ARROW_SPEED.get().floatValue()
                speedIncreasePerHit = ServerConfig.POOR_ARROW_SPEED_INCREASE.get().floatValue()
                maxArrowSpeed = ServerConfig.POOR_MAX_ARROW_SPEED.get().floatValue()
                zoneShrinkFactor = ServerConfig.POOR_ZONE_SHRINK_FACTOR.get().floatValue()
                perfectZoneStart = (100 - ServerConfig.POOR_ZONE_STARTING_SIZE.get()) / 2
                perfectZoneEnd = (100 + ServerConfig.POOR_ZONE_STARTING_SIZE.get()) / 2
                goodZoneStart = Math.max((100 - ServerConfig.POOR_ZONE_STARTING_SIZE.get() * 3) / 2, 1)
                goodZoneEnd = Math.min((100 + ServerConfig.POOR_ZONE_STARTING_SIZE.get() * 3) / 2, 100)
                minPerfectSize = ServerConfig.POOR_MIN_PERFECT_ZONE.get()
                perfectZoneSize = (perfectZoneEnd - perfectZoneStart).toFloat()
            }

             */
        }
    }

    @SubscribeEvent
    @JvmStatic
    fun onClientTick(event: ClientTickEvent.Post?) {
        // ensureInitialized();
        val mc = Minecraft.getInstance()
        if (mc.player == null) return
        // Track crouch release so shift+click toggle requires a fresh shift press
        if (minigameStarted && !crouchReleasedSinceStart && !mc.player!!.isCrouching()) {
            crouchReleasedSinceStart = true
        }
        if (!mc.isPaused()) updatePopups()
        if (mc.isPaused() || !isVisible) return

        tickAccumulator++
        if (tickAccumulator < TICKS_PER_PRINT) return
        tickAccumulator = 0

        // Update arrow movement
        if (arrowPosition >= 100) {
            movingDown = true
        } else if (arrowPosition <= 1) {
            movingDown = false
        }

        // Determine movement based on current speed and direction
        val delta = arrowSpeed * (if (movingDown) -1 else 1)
        arrowPosition = max(1f, min(arrowPosition + delta, 100f))

        // Update popups
    }

    private fun updatePopups() {
        // Age existing popups - each tick is 50ms (20 TPS)
        var i = 0
        while (i < popups.size) {
            val popup = popups[i]
            popup.age += 50f // 50ms per tick
            if (popup.age >= POPUP_DURATION_MS) {
                popups.removeAt(i--)
            }
            i++
        }
    }

    fun triggerPopup(text: Component?) {
        popups.add(Popup(text))
    }

    fun speedUp() {
        arrowSpeed = min(arrowSpeed + speedIncreasePerHit, maxArrowSpeed)
    }

    /*
    fun setIsVisible(pos: BlockPos, isVisible: Boolean) {
        TableMinigameEvent.isVisible = isVisible
        PacketDistributor.sendToServer(UpgradeRequestPacketPayload(pos))
    }

     */

    fun resetPopUps() {
        popups.clear()
    }

    @JvmOverloads
    fun reset(blueprintQuality: String? = null) {
        isVisible = false
        minigameStarted = false
        crouchReleasedSinceStart = false
        hitsRemaining = 0
        perfectHits = 0
        goodHits = 0
        missedHits = 0
        arrowPosition = 50f
        movingDown = false
        currentPerfectZoneSize = 0f
        currentGoodZoneSize = 0f
        lastPerfect = 0
        lastGood = 0
        lastMiss = 0

        setupForQuality(blueprintQuality ?: "none") // 🔥 initialize from blueprint

        randomizeCenter()
    }

    // Utility clamp
    private fun clamp(value: Int, min: Int, max: Int): Int {
        return max(min, min(max, value))
    }

    private fun randomizeCenter() {
        // Randomize zone center directly
        val randomCenter = 20 + Math.random().toFloat() * (60) // random between 20 and 80
        val zoneSize = (perfectZoneEnd - perfectZoneStart).toFloat()
        val goodZoneSize = (goodZoneEnd - goodZoneStart).toFloat()

        val halfPerfect = (zoneSize / 2f).toInt()
        val halfGood = (goodZoneSize / 2f).toInt()

        perfectZoneStart = clamp(randomCenter.toInt() - halfPerfect, 0, 100)
        perfectZoneEnd = clamp(randomCenter.toInt() + halfPerfect, 0, 100)
        goodZoneStart = clamp(randomCenter.toInt() - halfGood, 0, 100)
        goodZoneEnd = clamp(randomCenter.toInt() + halfGood, 0, 100)
    }

    /*
    fun getHitsRemaining(): Int {
        return hitsRemaining
    }

     */

    /*
    fun handleHit(): Boolean? {
        arrowSpeed = min(arrowSpeed + speedIncreasePerHit, maxArrowSpeed)

        if (arrowPosition >= perfectZoneStart && arrowPosition <= perfectZoneEnd) {
            perfectHits++
            lastPerfect = perfectHits

            triggerPopup(
                Component.translatable("overgeared.forging.perfect")
                    .withStyle { s -> s.withBold(true).withColor(0xFFD700) }
            )

        } else if (arrowPosition >= goodZoneStart && arrowPosition <= goodZoneEnd) {
            goodHits++
            lastGood = goodHits

            triggerPopup(
                Component.translatable("overgeared.forging.good")
                    .withStyle { s -> s.withBold(true).withColor(0x55FF55) }
            )

        } else {
            // ミス時の処理
            missedHits++
            lastMiss = missedHits

            triggerPopup(
                Component.translatable("overgeared.forging.miss")
                    .withStyle { s -> s.withBold(true).withColor(0xFF5555) }
            )

            // 【追加】ミスした瞬間に即座に終了処理を呼び出し、falseを返します
            return finish()
        }

        shrinkAndShiftZones()
        hitsRemaining--

        return if (hitsRemaining <= 0) {
            finish()
        } else {
            null
        }
    }
    */

    fun handleHit(): Boolean? {
        arrowSpeed = min(arrowSpeed + speedIncreasePerHit, maxArrowSpeed)

        if (arrowPosition >= perfectZoneStart && arrowPosition <= perfectZoneEnd) {
            perfectHits++
            lastPerfect = perfectHits

            triggerPopup(
                Component.translatable("overgeared.forging.perfect")
                    .withStyle { s -> s.withBold(true).withColor(0xFFD700) }
            )

        } else if (arrowPosition >= goodZoneStart && arrowPosition <= goodZoneEnd) {
            goodHits++
            lastGood = goodHits

            triggerPopup(
                Component.translatable("overgeared.forging.good")
                    .withStyle { s -> s.withBold(true).withColor(0x55FF55) }
            )

        } else {
            missedHits++
            lastMiss = missedHits

            triggerPopup(
                Component.translatable("overgeared.forging.miss")
                    .withStyle { s -> s.withBold(true).withColor(0xFF5555) }
            )
        }

        shrinkAndShiftZones()
        hitsRemaining--

        return if (hitsRemaining <= 0) {
            finish()
        } else {
            null
        }
    }
    /*
    fun setHitsRemaining(hitsRemaining: Int) {
        TableMinigameEvent.hitsRemaining = hitsRemaining
        maxHits = hitsRemaining
    }

     */

    fun start(
        quality: String,
        hits: Int
    ) {
        println("MINIGAME START")

        reset(quality)
        hitsRemaining = hits

        isVisible = true
        minigameStarted = true

        println("VISIBLE: $isVisible")
    }

    fun finishForging(): String {
        isVisible = false
        minigameStarted = false
        val totalHits = perfectHits + goodHits + missedHits
        var qualityScore = 0f
        if (totalHits > 0) qualityScore = (perfectHits * 1.0f + goodHits * 0.6f) / totalHits
        if (qualityScore > 0.9) return "perfect"
        if (qualityScore > 0.6) return "expert"
        if (qualityScore > 0.3) return "well"
        return "poor"
    }

    fun finish(): Boolean {

        isVisible = false

        minigameStarted = false



        val totalHits = perfectHits + goodHits + missedHits



        if (totalHits <= 0) {

            return false

        }



// Perfect + Good を成功扱い

        val successHits = perfectHits + goodHits



        return successHits.toFloat() / totalHits.toFloat() >= 0.6f

    }


    // Add two static fields to track current sizes independently of clamped values:
    private var currentPerfectZoneSize = 0f
    private var currentGoodZoneSize = 0f

    fun shrinkAndShiftZones() {
        // Initialize once if not yet done
        if (currentPerfectZoneSize == 0f || currentGoodZoneSize == 0f) {
            currentPerfectZoneSize = (perfectZoneEnd - perfectZoneStart).toFloat()
            currentGoodZoneSize = (goodZoneEnd - goodZoneStart).toFloat()
        }

        // --- Step 1: Shrink zones based on stored sizes ---
        currentPerfectZoneSize = max(minPerfectSize, currentPerfectZoneSize * zoneShrinkFactor)
        currentGoodZoneSize = max(currentPerfectZoneSize * 3, currentGoodZoneSize * zoneShrinkFactor)

        // --- Step 2: Get old data for comparison ---
        val oldPerfectCenter = (perfectZoneStart + perfectZoneEnd) / 2f
        val oldGoodCenter = (goodZoneStart + goodZoneEnd) / 2f
        val oldPerfectStart = perfectZoneStart
        val oldPerfectEnd = perfectZoneEnd
        val oldGoodStart = goodZoneStart
        val oldGoodEnd = goodZoneEnd

        // --- Step 3: Attempt to find a new valid zone placement ---
        var attempts = 0
        var newPerfectStart = perfectZoneStart
        var newPerfectEnd = perfectZoneEnd
        var newGoodStart = goodZoneStart
        var newGoodEnd = goodZoneEnd

        while (attempts < 30) {
            attempts++

            // Weighted random center: bias near middle but allow full range
            val newCenter = getWeightedRandomCenter(50f)

            // Compute bounds
            var pStart = (newCenter - currentPerfectZoneSize / 2).roundToInt()
            var pEnd = (newCenter + currentPerfectZoneSize / 2).roundToInt()
            var gStart = (newCenter - currentGoodZoneSize / 2).roundToInt()
            var gEnd = (newCenter + currentGoodZoneSize / 2).roundToInt()

            // Clamp to 0–100 range
            pStart = clamp(pStart, 0, 100)
            pEnd = clamp(pEnd, 0, 100)
            gStart = clamp(gStart, 0, 100)
            gEnd = clamp(gEnd, 0, 100)

            val newPerfectCenter = (pStart + pEnd) / 2f
            val newGoodCenter = (gStart + gEnd) / 2f

            // --- Step 4: Check separation conditions ---
            // Tolerances (in percent of bar width)
            val minCenterDiff = 5f // must move at least this much from previous center
            val minEdgeDiff = 3f // must move edges by at least this much

            val perfectTooClose =
                abs(newPerfectCenter - oldPerfectCenter) < minCenterDiff || abs(pStart - oldPerfectStart) < minEdgeDiff || abs(
                    pEnd - oldPerfectEnd
                ) < minEdgeDiff

            val goodTooClose =
                abs(newGoodCenter - oldGoodCenter) < minCenterDiff || abs(gStart - oldGoodStart) < minEdgeDiff || abs(
                    gEnd - oldGoodEnd
                ) < minEdgeDiff

            // Regenerate if *either* zone is too close
            if (perfectTooClose || goodTooClose) continue

            // --- Step 5: Accept the new zones ---
            newPerfectStart = pStart
            newPerfectEnd = pEnd
            newGoodStart = gStart
            newGoodEnd = gEnd
            break
        }

        // --- Step 6: Apply the new zones ---
        perfectZoneStart = newPerfectStart
        perfectZoneEnd = newPerfectEnd
        goodZoneStart = newGoodStart
        goodZoneEnd = newGoodEnd
    }

    private fun edgesTooCloseOrOverlapping(
        newStart: Int, newEnd: Int, oldStart: Int, oldEnd: Int, overlapRatio: Float,
        clampedLeft: Boolean, clampedRight: Boolean
    ): Boolean {
        val newSize = max(1, newEnd - newStart)
        val tolerance = 1
        val startClose = abs(newStart - oldStart) <= tolerance
        val endClose = abs(newEnd - oldEnd) <= tolerance

        val overlap = min(newEnd, oldEnd) - max(newStart, oldStart)
        val overlapFraction = if (overlap > 0) overlap.toFloat() / newSize else 0f

        // Edge fix: allow shifts even if clamped edges stay constant
        if ((clampedLeft && newStart == 0 && oldStart == 0) ||
            (clampedRight && newEnd == 100 && oldEnd == 100)
        ) {
            // If one edge is pinned, only compare the *free* edge
            return overlapFraction > overlapRatio &&
                    ((clampedLeft && endClose) || (clampedRight && startClose))
        }

        return (startClose && endClose) || overlapFraction > overlapRatio
    }

    private fun getWeightedRandomCenter(bias: Float): Float {
        // bias toward middle but allow full 0–100 range
        val rand = Math.random().toFloat()
        val weighted = rand.toDouble().pow(1.5).toFloat() // tweak exponent for more/less bias
        return bias + (weighted - 0.5f) * 100f
    }
    /*

    fun getAnvilPos(playerId: UUID?): BlockPos {
        return ModItemInteractEvents.playerAnvilPositions.getOrDefault(playerId, BlockPos.ZERO)
    }

    fun setAnvilPos(playerId: UUID?, pos: BlockPos?) {
        ModItemInteractEvents.playerAnvilPositions.put(playerId, pos)
    }

    fun clearAnvilPos(playerId: UUID?) {
        ModItemInteractEvents.playerAnvilPositions.remove(playerId)
    }

     */


    fun setMinigameStarted(pos: BlockPos?, minigameStarted: Boolean) {
        // Only reset crouch flag on fresh start (not on redundant S2C confirmations)
        if (minigameStarted && !TableMinigameEvent.minigameStarted) {
            crouchReleasedSinceStart = false
        }
        TableMinigameEvent.minigameStarted = minigameStarted
    }

    /*
    fun hasAnvilPosition(playerId: UUID?): Boolean {
        val pos: BlockPos? = ModItemInteractEvents.playerAnvilPositions.get(playerId)
        return pos != null && pos != BlockPos.ZERO
    }



    // ✅ Player-specific hide
    fun hideMinigame(playerId: UUID?) {
        isVisible = false
        val pos: BlockPos? = ModItemInteractEvents.playerAnvilPositions.get(playerId)
        if (pos != null && pos != BlockPos.ZERO) {
            PacketDistributor.sendToServer(SetMinigameVisibleC2SPacket(false, pos))
        }
        // clearAnvilPos(playerId);
    }

     */


    // ===============================
    // Popup class
    // ===============================
    class Popup(val text: Component?) {
        var age: Float = 0f // milliseconds
    }


}