# R8-regels voor de release-build (isMinifyEnabled = true).
# Bibliotheken (Compose, AndroidX, Billing, Firebase, kotlinx-serialization) leveren hun eigen consumer-regels mee.

# Leesbare stacktraces in Play Console (de mapping zit automatisch in de AAB).
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-dontwarn org.osmdroid.**
