// PRIORITY: LOW
// AFTER-WARNING: Check for instance is always 'false'
// AFTER-WARNING: Check for instance is always 'true'
fun test(n: Int): String {
    return <caret>when (n) {
        !is Int -> "???"
        in 0..10 -> "small"
        in 10..100 -> "average"
        in 100..1000 -> "big"
        1000000 -> "million"
        !in -100..-10 -> "good"
        is Int -> "unknown"
        else -> "unknown"
    }
}