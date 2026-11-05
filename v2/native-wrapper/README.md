# NICE ELECTRICAL PRO V2 — Android APK

This folder contains the Capacitor Android wrapper. It packages the existing V2 web application as a locally bundled Android app; it does not replace the GitHub Pages PWA or V1.

The APK is built by GitHub Actions. The initial workflow produces a debug-signed APK for controlled installation/testing. A production release requires a separately managed signing key and release process.

Important: Android app storage is separate from Chrome's website storage. Export a JSON backup from the current shortcut before moving real records into the APK, then import it into the APK after installation. Cloud sync is not yet enabled.
