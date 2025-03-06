# Module internal-cinterop

An internal module for which `crypto-rand` depends on. This is to transparently "hide" cinterop from 
`crypto-rand` consumers because Kotlin's generated `.knm` files are all `public` visibility.

This publication SHOULD NOT BE USED and will go away when [KT-75722](https://youtrack.jetbrains.com/issue/KT-75722) 
is resolved.
