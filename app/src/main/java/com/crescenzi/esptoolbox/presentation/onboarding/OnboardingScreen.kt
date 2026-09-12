package com.crescenzi.esptoolbox.presentation.onboarding

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crescenzi.esptoolbox.R
import com.crescenzi.esptoolbox.presentation.main_shell.LocalNavController
import com.crescenzi.esptoolbox.presentation.main_shell.OnboardingPage
import com.crescenzi.esptoolbox.presentation.main_shell.UsbPage
import com.crescenzi.esptoolbox.presentation.widget.AppButton
import com.crescenzi.esptoolbox.presentation.widget.AppButtonType
import com.crescenzi.esptoolbox.presentation.widget.AppScaffold
import com.crescenzi.esptoolbox.theme.CONTENT_TOP_PADDING
import com.crescenzi.esptoolbox.theme.LATERAL_PADDING
import com.crescenzi.esptoolbox.theme.SPACE_L
import com.crescenzi.esptoolbox.theme.SPACE_M
import com.crescenzi.esptoolbox.theme.SPACE_S
import com.crescenzi.esptoolbox.theme.SPACE_XL


/**
 * Page used for all onboarding permission checks
 */
@Composable
fun OnboardingScreen(
    onboardingViewModel: OnboardingViewModel
) {

    val activity = LocalActivity.current

    LaunchedEffect(activity) {
        onboardingViewModel.onReqPermissionCallback = {
            activity?.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", activity.packageName, null)
                }
            )
        }
    }

    val navController = LocalNavController.current

    val internetState = onboardingViewModel.deviceHardwareStatus.internet.collectAsStateWithLifecycle().value
    //SSID and BSSID detection
    val locationPermissionState =
        onboardingViewModel.deviceHardwareStatus.locationPermission.collectAsStateWithLifecycle().value
    val locationState = onboardingViewModel.deviceHardwareStatus.location.collectAsStateWithLifecycle().value

    /**
     * If EVERY requirement is met, proceed
     */
    val allRequirementsMet = internetState && locationPermissionState && locationState
    val activeStep = OnboardingStepId.entries.firstOrNull { step ->
        when (step) {
            OnboardingStepId.INTERNET -> !internetState
            OnboardingStepId.LOCATION_PERMISSION -> !locationPermissionState
            OnboardingStepId.LOCATION -> !locationState
        }
    }

    AppScaffold(
        title = stringResource(R.string.get_started_tool),
        reserveTopBarSpace = true,
        scrollable = false,
        bottomBar = {
            AppButton(
                txt = stringResource(R.string.go_tool),
                enabled = allRequirementsMet,
                onTap = {
                    if (allRequirementsMet) {
                        navController.navigate(UsbPage) {
                            popUpTo(OnboardingPage) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = CONTENT_TOP_PADDING)
        ) {

            Column(
                modifier = Modifier.padding(horizontal = LATERAL_PADDING),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                OnboardingStep(
                    id = OnboardingStepId.INTERNET,
                    icon = Icons.Rounded.Wifi,
                    completed = internetState,
                    active = activeStep == OnboardingStepId.INTERNET,
                    title = stringResource(R.string.internet_req_title),
                    body = stringResource(R.string.internet_req_body),
                )

                OnboardingStep(
                    id = OnboardingStepId.LOCATION_PERMISSION,
                    icon = Icons.Rounded.LocationOn,
                    completed = locationPermissionState,
                    active = activeStep == OnboardingStepId.LOCATION_PERMISSION,
                    title = stringResource(R.string.location_permission_req_title),
                    body = null,
                ) {
                    AppButton(
                        txt = stringResource(R.string.grant_permission_tool),
                        type = AppButtonType.OUTLINED,
                        fillWidth = false,
                        onTap = onboardingViewModel::callReqPermission
                    )
                }

                OnboardingStep(
                    id = OnboardingStepId.LOCATION,
                    icon = Icons.Rounded.MyLocation,
                    completed = locationState,
                    active = activeStep == OnboardingStepId.LOCATION,
                    title = stringResource(R.string.location_req_title),
                    body = stringResource(R.string.location_req_body),
                    isLast = true,
                )
            }
        }
    }
}

private enum class OnboardingStepId {
    INTERNET, LOCATION_PERMISSION, LOCATION
}

@Composable
private fun OnboardingStep(
    id: OnboardingStepId,
    icon: ImageVector,
    completed: Boolean,
    active: Boolean,
    title: String,
    body: String?,
    isLast: Boolean = false,
    content: @Composable (() -> Unit)? = null,
) {
    val colorScheme = MaterialTheme.colorScheme
    val stepIcon = if (completed) Icons.Rounded.Check else icon
    val motion = tween<androidx.compose.ui.unit.Dp>(
        durationMillis = 320,
        easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    )
    val stepHeight by animateDpAsState(
        targetValue = if (active) 216.dp else 80.dp,
        animationSpec = motion,
        label = "${id.name}_height"
    )
    val lineGap by animateDpAsState(
        targetValue = if (active) SPACE_S else SPACE_M,
        animationSpec = motion,
        label = "${id.name}_line_gap"
    )
    val titleTopPadding by animateDpAsState(
        targetValue = if (active) 0.dp else SPACE_S,
        animationSpec = motion,
        label = "${id.name}_title_padding"
    )
    val circleColor by animateColorAsState(
        targetValue = if (active || completed) colorScheme.secondary else colorScheme.surfaceContainerHighest,
        animationSpec = tween(durationMillis = 220),
        label = "${id.name}_circle_color"
    )
    val contentArrangement = when {
        active && isLast -> Arrangement.Bottom
        active -> Arrangement.Center
        else -> Arrangement.Top
    }
    val contentBottomPadding = when {
        active && isLast -> SPACE_XL
        isLast -> 0.dp
        else -> SPACE_M
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(SPACE_L)
    ) {
        Column(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (active && isLast) {
                TimelineLine(
                    modifier = Modifier.weight(1f)
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(circleColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stepIcon,
                    contentDescription = null,
                    tint = colorScheme.onSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            if (!isLast) {
                TimelineLine(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = lineGap)
                )
            } else if (active) {
                Spacer(modifier = Modifier.height(SPACE_XL + SPACE_L))
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = stepHeight)
                .padding(bottom = contentBottomPadding),
            verticalArrangement = contentArrangement
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(top = titleTopPadding)
            )

            AnimatedVisibility(
                visible = active,
                enter = expandVertically(
                    animationSpec = tween(
                        durationMillis = 260,
                        easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
                    ),
                    expandFrom = Alignment.CenterVertically
                ),
                exit = shrinkVertically(
                    animationSpec = tween(
                        durationMillis = 220,
                        easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
                    ),
                    shrinkTowards = Alignment.CenterVertically
                )
            ) {
                Column(
                    modifier = Modifier.padding(top = SPACE_S),
                    verticalArrangement = Arrangement.spacedBy(SPACE_M)
                ) {
                    body?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    content?.invoke()
                }
            }
        }
    }
}

@Composable
private fun TimelineLine(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(1.dp)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.65f))
    )
}
