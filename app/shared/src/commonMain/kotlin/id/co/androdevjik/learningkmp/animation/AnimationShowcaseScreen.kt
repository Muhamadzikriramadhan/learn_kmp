package id.co.androdevjik.learningkmp.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * =========================================================================
 * 1. MICRO-INTERACTIONS & SPRING PHYSICS
 * =========================================================================
 * Rahasia UI yang terasa "hidup" dan "taktil" seperti di iOS / Material 3 Expressive
 * adalah penggunaan Physics-based animation (Spring) dibandingkan durasi linear kaku (Tween).
 */

/**
 * Kartu interaktif dengan efek tekan mengecil (Scale on Press) menggunakan Spring.
 * Menggunakan Modifier.graphicsLayer agar transformasi berjalan langsung di GPU
 * tanpa memicu relayout/recomposition CPU (Smooth 60/120 FPS).
 */
@Composable
fun BouncyInteractiveCard(
    title: String,
    subtitle: String,
    dampingRatio: Float,
    stiffness: Float,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animasi scale dengan spring fisika
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(
            dampingRatio = dampingRatio,
            stiffness = stiffness
        ),
        label = "CardPressScale"
    )

    // Animasi elevasi kartu saat ditekan
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        animationSpec = spring(dampingRatio = dampingRatio, stiffness = stiffness),
        label = "CardPressElevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Custom visual feedback via scale
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E2230)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        color = Color(0xFF9AA4B2),
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isPressed) "👆" else "✨",
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isPressed) "Status: Ditekan (Scaling to 0.94x)..." else "Coba tekan & tahan kartu ini untuk merasakan feedback taktil!",
                color = if (isPressed) Color(0xFF38BDF8) else Color(0xFF64748B),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Custom Toggle Switch dengan animasi sliding pill dan color morphing.
 */
@Composable
fun FluidToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    dampingRatio: Float,
    stiffness: Float
) {
    // Animasi perpindahan posisi pill horizontal
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 28.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = dampingRatio,
            stiffness = stiffness
        ),
        label = "ToggleThumbOffset"
    )

    // Animasi morphing warna background
    val trackColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF10B981) else Color(0xFF334155),
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "ToggleTrackColor"
    )

    // Animasi peregangan thumb (squash & stretch) saat bergerak
    val thumbWidth by animateDpAsState(
        targetValue = 24.dp,
        animationSpec = spring(dampingRatio = dampingRatio, stiffness = stiffness),
        label = "ThumbWidth"
    )

    Box(
        modifier = Modifier
            .width(56.dp)
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(trackColor)
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffset.roundToPx(), 0) }
                .size(width = thumbWidth, height = 26.dp)
                .clip(CircleShape)
                .background(Color.White)
                .shadow(elevation = 3.dp, shape = CircleShape)
        )
    }
}

/**
 * Like Button dengan efek detak jantung (Heartbeat Multi-phase Bounce)
 */
@Composable
fun HeartbeatLikeButton(
    isLiked: Boolean,
    onToggle: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isLiked) Color(0xFFEF4444).copy(alpha = 0.15f) else Color(0xFF1E293B))
            .clickable {
                onToggle()
                // Choreography animasi: Membesar cepat (1.35x), lalu membal ke 1.0f
                scope.launch {
                    scale.snapTo(0.75f)
                    scale.animateTo(
                        targetValue = 1.35f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioHighBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                    scale.animateTo(
                        targetValue = 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isLiked) "❤️" else "🤍",
            fontSize = 24.sp,
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
        )
    }
}

/**
 * Modern Shimmer Skeleton Loading Effect
 * Menggunakan rememberInfiniteTransition untuk animasi linear sweep gradient tanpa beban memori.
 */
@Composable
fun ShimmerSkeletonLoader() {
    val transition = rememberInfiniteTransition(label = "ShimmerLoop")
    val translateAnim by transition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1E293B),
            Color(0xFF334155),
            Color(0xFF1E293B)
        ),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 300f, 0f)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF141A29))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(shimmerBrush)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(shimmerBrush)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(shimmerBrush)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(shimmerBrush)
        )
    }
}

/**
 * =========================================================================
 * 2. EXPANDABLE CARD & STAGGERED ENTRANCE
 * =========================================================================
 */

/**
 * Kartu Expandable yang mulus menggunakan Modifier.animateContentSize()
 * dan rotasi panah chevron dengan animateFloatAsState.
 */
@Composable
fun ExpandableDetailCard(
    title: String,
    description: String,
    bulletPoints: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    // Rotasi panah dari 0° sampai 180°
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ChevronRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            // KUNCI: animateContentSize otomatis menghitung perubahan tinggi layout dengan spring
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F2E))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // Panah indikator rotasi
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF283046))
                        .graphicsLayer { rotationZ = rotationAngle },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "▼", color = Color(0xFF60A5FA), fontSize = 12.sp)
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(200)) + expandVertically(
                    animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                ),
                exit = fadeOut(animationSpec = tween(150)) + shrinkVertically(
                    animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
                )
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF2D3748))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = description,
                        color = Color(0xFFCBD5E1),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    bulletPoints.forEach { point ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text(text = "• ", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                            Text(text = point, color = Color(0xFF94A3B8), fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * =========================================================================
 * 3. ANIMATED CONTENT & DYNAMIC TAB SELECTOR
 * =========================================================================
 */

/**
 * Animated Pill Tab Bar dengan fluid sliding indicator menggunakan animateDpAsState
 */
@Composable
fun FluidTabSelector(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF111827))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedIndex == index

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFF4F46E5) else Color.Transparent,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "TabBgColor"
            )

            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color(0xFF94A3B8),
                animationSpec = tween(200),
                label = "TabTextColor"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(backgroundColor)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Odometer-style Animated Number Counter
 * Menggunakan AnimatedContent untuk efek angka bergeser ke atas/bawah secara dinamis.
 */
@Composable
fun AnimatedStatCounter(
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E2230))
            .padding(16.dp)
    ) {
        Column {
            Text(text = "Dynamic Odometer Counter", color = Color(0xFF94A3B8), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))

            // AnimatedContent dengan arah transisi vertikal
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    if (targetState > initialState) {
                        // Menambah: Masuk dari bawah, keluar ke atas
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> -height } + fadeOut()
                        )
                    } else {
                        // Mengurang: Masuk dari atas, keluar ke bawah
                        (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> height } + fadeOut()
                        )
                    }
                },
                label = "CounterAnimation"
            ) { targetNum ->
                Text(
                    text = "$targetNum",
                    color = Color(0xFF38BDF8),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF334155))
                    .clickable { onDecrement() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "-", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4F46E5))
                    .clickable { onIncrement() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * =========================================================================
 * 4. GESTURE & PHYSICS INTERACTION (DRAGGABLE SNAP-BACK CARD)
 * =========================================================================
 */

/**
 * Kartu Fisika Interaktif:
 * Bisa di-drag bebas dengan gestur pointerInput.
 * Ketika dilepas, otomatis kembali ke tengah dengan efek pegas elastis (Spring Physics).
 */
@Composable
fun PhysicsDraggableCard(
    dampingRatio: Float,
    stiffness: Float
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Panduan di background
        Text(
            text = "Tarik kartu ke mana saja lalu lepas!\nKartu akan membal dengan hukum pegas fisika.",
            color = Color(0xFF475569),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        offsetX.value.roundToInt(),
                        offsetY.value.roundToInt()
                    )
                }
                .graphicsLayer {
                    // Berikan tilt/kemiringan dinamis mengikuti drag X
                    rotationZ = (offsetX.value * 0.05f).coerceIn(-25f, 25f)
                }
                .size(170.dp, 100.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))
                    )
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            // Lepaskan: Snap back ke posisi (0, 0) dengan Spring
                            scope.launch {
                                launch {
                                    offsetX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = dampingRatio,
                                            stiffness = stiffness
                                        )
                                    )
                                }
                                launch {
                                    offsetY.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = dampingRatio,
                                            stiffness = stiffness
                                        )
                                    )
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount.x)
                                offsetY.snapTo(offsetY.value + dragAmount.y)
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🎯 Tarik Aku!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "X: ${offsetX.value.roundToInt()} | Y: ${offsetY.value.roundToInt()}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

/**
 * =========================================================================
 * 5. MAIN SHOWCASE SCREEN
 * =========================================================================
 */
@Composable
fun AnimationShowcaseScreen() {
    val tabs = listOf("Micro & Springs", "Cards & Content", "Physics & Drag")
    var selectedTab by remember { mutableIntStateOf(0) }

    // State untuk kontrol Tuning Pegas secara Live
    var dampingRatio by remember { mutableFloatStateOf(Spring.DampingRatioMediumBouncy) }
    var stiffness by remember { mutableFloatStateOf(Spring.StiffnessMedium) }

    // State untuk demo interaksi
    var isSwitchOn by remember { mutableStateOf(true) }
    var isLiked by remember { mutableStateOf(false) }
    var counterValue by remember { mutableIntStateOf(42) }
    var clickCount by remember { mutableIntStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0B0F19)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Compose Multiplatform",
                        color = Color(0xFF818CF8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Smooth UI Animations",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "120 FPS Ready",
                        color = Color(0xFF34D399),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Selector
            FluidTabSelector(
                tabs = tabs,
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Live Physics Tuning Panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151B2B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚙️ Live Spring Tuner",
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Damping: ${(dampingRatio * 10).roundToInt() / 10f} | Stiff: ${stiffness.roundToInt()}",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Damping Ratio (Tingkat Pantulan / Bounciness)",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                    Slider(
                        value = dampingRatio,
                        onValueChange = { dampingRatio = it },
                        valueRange = 0.2f..1.2f,
                        modifier = Modifier.height(30.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Stiffness (Kecepatan Reaksi Pegas)",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                    Slider(
                        value = stiffness,
                        onValueChange = { stiffness = it },
                        valueRange = 50f..1500f,
                        modifier = Modifier.height(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Body Content sesuai Tab
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    label = "TabContentSwitch"
                ) { currentTab ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        when (currentTab) {
                            0 -> {
                                item {
                                    Text(
                                        text = "1. Micro-Interactions & Tactile Springs",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                item {
                                    BouncyInteractiveCard(
                                        title = "Taktil Press Feedback",
                                        subtitle = "Klik: $clickCount kali",
                                        dampingRatio = dampingRatio,
                                        stiffness = stiffness,
                                        onClick = { clickCount++ }
                                    )
                                }
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(18.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2230))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Fluid Spring Switch",
                                                    color = Color.White,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = if (isSwitchOn) "Status: Nyala (Fluid Slide)" else "Status: Mati",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 12.sp
                                                )
                                            }
                                            FluidToggleSwitch(
                                                checked = isSwitchOn,
                                                onCheckedChange = { isSwitchOn = it },
                                                dampingRatio = dampingRatio,
                                                stiffness = stiffness
                                            )
                                        }
                                    }
                                }
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(18.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2230))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Heartbeat Scale Animation",
                                                    color = Color.White,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = if (isLiked) "Disukai! (Animatable Sequence)" else "Ketuk hati untuk memberi like",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 12.sp
                                                )
                                            }
                                            HeartbeatLikeButton(
                                                isLiked = isLiked,
                                                onToggle = { isLiked = !isLiked }
                                            )
                                        }
                                    }
                                }
                                item {
                                    Text(
                                        text = "Skeleton Loading (Infinite Loop)",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ShimmerSkeletonLoader()
                                }
                            }
                            1 -> {
                                item {
                                    Text(
                                        text = "2. Expandable Cards & Content Morphing",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                item {
                                    ExpandableDetailCard(
                                        title = "Kenapa Spring > Duration Tween?",
                                        description = "Dalam fisika dunia nyata, benda tidak berhenti secara mendadak atau bergerak dengan durasi kaku 300ms. Spring meniru elastisitas pegas:",
                                        bulletPoints = listOf(
                                            "Bisa diinterupsi kapan saja tanpa visual glitch",
                                            "Memiliki momentum kecepatan (velocity transfer)",
                                            "Membuat UI terasa organik, responsif, dan premium"
                                        )
                                    )
                                }
                                item {
                                    ExpandableDetailCard(
                                        title = "Rahasia 120 FPS: graphicsLayer",
                                        description = "Hindari mengubah Modifier.offset() atau Modifier.size() secara langsung saat menganimasikan posisi/ukuran yang sering berubah:",
                                        bulletPoints = listOf(
                                            "Modifier.graphicsLayer { ... } membaca state di Draw Phase",
                                            "Melewati Composition Phase & Layout Phase sepenuhnya",
                                            "Dirender langsung oleh GPU hardware acceleration"
                                        )
                                    )
                                }
                                item {
                                    AnimatedStatCounter(
                                        value = counterValue,
                                        onIncrement = { counterValue++ },
                                        onDecrement = { counterValue-- }
                                    )
                                }
                            }
                            2 -> {
                                item {
                                    Text(
                                        text = "3. Interactive Physics & Gestures",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                item {
                                    PhysicsDraggableCard(
                                        dampingRatio = dampingRatio,
                                        stiffness = stiffness
                                    )
                                }
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2230))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = "💡 Prinsip Gesture + Animation",
                                                color = Color(0xFF38BDF8),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Ketika jari menyentuh layar, gunakan `Animatable.snapTo(dragAmount)` agar kartu menempel persis di ujung jari tanpa lag. Ketika dilepas (`onDragEnd`), panggil `animateTo(0f, spring(...))` untuk mengembalikan objek dengan momentum pegas.",
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 13.sp,
                                                lineHeight = 19.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
