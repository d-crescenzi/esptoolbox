package com.crescenzi.esptoolbox.presentation.main_shell

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.crescenzi.esptoolbox.R
import com.crescenzi.esptoolbox.core.AppConstants.NAV_DOT_COLOR
import com.crescenzi.esptoolbox.core.AppConstants.NAV_DOT_OFFSET
import com.crescenzi.esp32.LogRepo
import com.crescenzi.esptoolbox.presentation.DeviceHardwareStatus
import com.crescenzi.esptoolbox.presentation.onboarding.OnboardingScreen
import com.crescenzi.esptoolbox.presentation.requirement.RequirementScreen
import com.crescenzi.esptoolbox.presentation.main_shell.logs.LogScreen
import com.crescenzi.esptoolbox.presentation.main_shell.usb_connection.USBConnectionScreen
import com.crescenzi.esptoolbox.presentation.main_shell.usb_flash.USBFlashScreen
import com.crescenzi.esptoolbox.presentation.main_shell.wifi_connection.WIFIConnectionScreen
import com.crescenzi.esptoolbox.theme.NAV_ITEM_SIZE
import com.crescenzi.esptoolbox.theme.NAV_ITEM_SPACING
import com.crescenzi.esptoolbox.theme.NAV_PILL_HEIGHT
import com.crescenzi.esptoolbox.theme.NAV_PILL_PADDING
import com.crescenzi.esptoolbox.theme.NAV_PILL_RADIUS
import com.crescenzi.esptoolbox.theme.SPACE_XS
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Serializable
object OnboardingPage

@Serializable
object UsbPage

@Serializable
object FlashPage

@Serializable
object LogPage

@Serializable
object WifiPage

@Serializable
data class RequirementPage(val titleRes: Int, val subtitleRes: Int)


val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("LocalNavController is not provided.")
}

internal const val ONBOARDING_NAV_PILL_SHARED_KEY = "onboarding_nav_pill"


private enum class Workspace {
    USB, FLASH, LOG, WIFI;

    fun destination(): Any = when (this) {
        USB -> UsbPage
        FLASH -> FlashPage
        LOG -> LogPage
        WIFI -> WifiPage
    }

    @Composable
    fun label(): String = when (this) {
        USB -> stringResource(R.string.usb_label)
        FLASH -> stringResource(R.string.updater_label)
        LOG -> stringResource(R.string.log_label)
        WIFI -> stringResource(R.string.wifi_label)
    }

    @DrawableRes
    fun icon(): Int = when (this) {
        USB -> R.drawable.avd_nav_usb
        FLASH -> R.drawable.avd_nav_flash
        LOG -> R.drawable.avd_nav_log
        WIFI -> R.drawable.avd_nav_wifi
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainShell(
    onReqUsbPermission: () -> Unit
) {

    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        val current = navController.currentDestination
        if (current == null || current.route == null) {
            navController.navigate(OnboardingPage) {
                popUpTo(0)
            }
        }
    }

    val logRepo = koinInject<LogRepo>()
    val deviceHardwareStatus = koinInject<DeviceHardwareStatus>()

    val showBadge = rememberSaveable { mutableStateOf(false) }
    val logs by logRepo.logs.collectAsStateWithLifecycle()
    val locationEnabled by deviceHardwareStatus.location.collectAsStateWithLifecycle()
    val locationPermission by deviceHardwareStatus.locationPermission.collectAsStateWithLifecycle()
    val internetEnabled by deviceHardwareStatus.internet.collectAsStateWithLifecycle()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val currentWorkspace = when {
        destination?.hasRoute(UsbPage::class) == true -> Workspace.USB
        destination?.hasRoute(FlashPage::class) == true -> Workspace.FLASH
        destination?.hasRoute(LogPage::class) == true -> Workspace.LOG
        destination?.hasRoute(WifiPage::class) == true -> Workspace.WIFI
        else -> null
    }

    LaunchedEffect(logs.size) {
        if (currentWorkspace != Workspace.LOG) {
            showBadge.value = true
        }
    }

    /**
     * If a requirement gets lost anywhere past the onboarding checks, forward to the blocking
     * page and empty the back stack
     */
    LaunchedEffect(locationEnabled, locationPermission, internetEnabled, currentWorkspace) {
        if (currentWorkspace != null) {
            val requirement = when {
                !locationPermission -> RequirementPage(
                    titleRes = R.string.permission_required,
                    subtitleRes = R.string.permission_lost_desc
                )

                !locationEnabled -> RequirementPage(
                    titleRes = R.string.location_lost_title,
                    subtitleRes = R.string.location_lost_desc
                )

                !internetEnabled -> RequirementPage(
                    titleRes = R.string.internet_lost_title,
                    subtitleRes = R.string.internet_lost_desc
                )

                else -> null
            }
            requirement?.let {
                navController.navigate(it) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    val haptic = LocalHapticFeedback.current

    CompositionLocalProvider(LocalNavController provides navController) {
        SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
            val sharedTransitionScope = this

            Box(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    NavHost(
                        navController = navController,
                        startDestination = OnboardingPage,
                        modifier = Modifier.weight(1f)
                    ) {
                        composable<OnboardingPage> {
                            OnboardingScreen(
                                onboardingViewModel = koinViewModel(viewModelStoreOwner = it),
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = this
                            )
                        }
                        composable<UsbPage> {
                            USBConnectionScreen(
                                usbConnectionViewModel = koinViewModel(viewModelStoreOwner = it),
                                onReqUsbPermission = onReqUsbPermission
                            )
                        }
                        composable<FlashPage> {
                            USBFlashScreen(koinViewModel(viewModelStoreOwner = it))
                        }
                        composable<LogPage> {
                            LogScreen(koinViewModel(viewModelStoreOwner = it))
                        }
                        composable<WifiPage> {
                            WIFIConnectionScreen(koinViewModel(viewModelStoreOwner = it))
                        }
                        composable<RequirementPage> {
                            val args = it.toRoute<RequirementPage>()
                            RequirementScreen(
                                titleRes = args.titleRes,
                                subtitleRes = args.subtitleRes
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = currentWorkspace != null,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    currentWorkspace?.let { selectedWorkspace ->
                        BottomNavigationPill(
                            currentWorkspace = selectedWorkspace,
                            showBadge = showBadge.value,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = this,
                            onWorkspaceClick = { workspace ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (selectedWorkspace != workspace) {
                                    navController.navigate(workspace.destination()) {
                                        popUpTo(OnboardingPage) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                                if (workspace == Workspace.LOG) {
                                    showBadge.value = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun BottomNavigationPill(
    currentWorkspace: Workspace,
    showBadge: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onWorkspaceClick: (Workspace) -> Unit,
) {
    val sharedModifier = with(sharedTransitionScope) {
        Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(ONBOARDING_NAV_PILL_SHARED_KEY),
            animatedVisibilityScope = animatedVisibilityScope,
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
            .padding(bottom = SPACE_XS),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = sharedModifier,
            shape = RoundedCornerShape(NAV_PILL_RADIUS),
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = SPACE_XS,
            shadowElevation = SPACE_XS,
        ) {
            Row(
                modifier = Modifier
                    .height(NAV_PILL_HEIGHT)
                    .padding(horizontal = NAV_PILL_PADDING),
                horizontalArrangement = Arrangement.spacedBy(NAV_ITEM_SPACING),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Workspace.entries.forEach { workspace ->
                    NavPillItem(
                        icon = workspace.icon(),
                        contentDescription = workspace.label(),
                        selected = currentWorkspace == workspace,
                        showDot = workspace == Workspace.LOG && showBadge,
                        onClick = {
                            onWorkspaceClick(workspace)
                        }
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@Composable
private fun NavPillItem(
    @DrawableRes icon: Int,
    contentDescription: String,
    selected: Boolean,
    showDot: Boolean,
    onClick: () -> Unit,
) {
    val transition = updateTransition(selected, label = "nav")
    val indicatorSize by transition.animateDp(label = "indicator") { if (it) NAV_ITEM_SIZE else 0.dp }
    val tint by transition.animateColor(label = "tint") {
        if (it) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    }
    val avd = AnimatedImageVector.animatedVectorResource(icon)
    var atEnd by remember { mutableStateOf(false) }
    val painter = rememberAnimatedVectorPainter(animatedImageVector = avd, atEnd = atEnd)

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(contentDescription) } },
        state = rememberTooltipState(),
    ) {
        Box(
            modifier = Modifier
                .size(NAV_ITEM_SIZE)
                .clip(CircleShape)
                .selectable(
                    selected = selected,
                    role = Role.Tab,
                    onClick = {
                        atEnd = !atEnd
                        onClick()
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(indicatorSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
            BadgedBox(
                badge = {
                    if (showDot) {
                        Badge(
                            containerColor = NAV_DOT_COLOR,
                            modifier = Modifier.offset(x = NAV_DOT_OFFSET, y = -NAV_DOT_OFFSET),
                        )
                    }
                },
            ) {
                Icon(
                    painter = painter,
                    contentDescription = contentDescription,
                    tint = tint,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
