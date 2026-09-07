# MRLB Business FINAL v1.2
Single Acode-editable Android project for MR Luxe Box & Bags.

## Included
- Customer CRUD + search
- Orders + order items
- Invoice/order data foundation
- Payments + partial payment
- Automatic due calculation
- Due reminder scheduling + Android notification permission handling
- WhatsApp share helper and SMS-ready Android intent foundation
- Expense categories
- Sales / collection / due / expense / estimated profit dashboard
- PDF report
- CSV export compatible with Excel
- Encrypted backup: AES-GCM with PBKDF2-HMAC-SHA256 derived key
- Android Storage Access Framework file picker for backup/restore
- PIN stored as SHA-256 hash, not plaintext
- PIN verification and app lock foundation
- Input validation foundation
- Local Room database
- Settings

## Build
Use Android Studio or a compatible Gradle Android environment:
`./gradlew assembleDebug`

## Important production notes
- For a real release, move secrets/crypto policy to a dedicated audited security layer and consider SQLCipher/Android Keystore for database-at-rest encryption.
- Restore should ideally create a pre-restore encrypted snapshot before replacement.
- SMS should use the Android SMS composer/permissions rather than silent sending.
