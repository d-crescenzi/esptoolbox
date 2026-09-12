# Project Working Notes

## Hard Constraints

- Do not commit or push unless the user explicitly asks.
- Do not edit the `esp32` module unless the user explicitly asks for work there.
- Treat onboarding, permissions, USB permission handling, and navigation stack behavior as delicate flows. Preserve existing business logic unless the requested change explicitly targets it.
- Keep changes narrowly scoped to the user request. Avoid opportunistic refactors.

## Android App Conventions

- The app is dark-only.
- Theme code lives under `app/src/main/java/com/crescenzi/esptoolbox/theme/`:
  - `AppTheme.kt` for the Compose theme wrapper.
  - `AppColor.kt` for color scheme definitions.
  - `AppDimen.kt` for shared spacing, sizing, and radius constants.
  - `AppType.kt` for typography.
- Shared UI components should use the `App*` naming style, for example `AppButton`, `AppScaffold`, and `AppTextField`.
- Prefer Material rounded, no-fill icons from Compose libraries for simple icons.
- Keep app colors centralized in the theme where possible. Avoid adding one-off hardcoded colors unless they are semantic, local, and justified.

## UI Change Checklist

- For Compose UI changes, run `./gradlew :app:compileDebugKotlin`.
- For visual changes, install and take screenshots on a device or emulator when available.
- Check mobile layouts for text clipping, overlap, and disabled/enabled contrast.
- Preserve existing haptics, button debounce, keyboard/focus behavior, and navigation semantics unless requested otherwise.

## Safety Checks

- Before finishing, check that `esp32` has no unintended diffs:
  - `git diff --name-only -- esp32`
- Mention any build warnings only if they are new or relevant to the requested change.
