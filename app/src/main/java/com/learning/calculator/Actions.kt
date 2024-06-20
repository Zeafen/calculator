package com.learning.calculator

enum class Actions(val actionTxt : String) {
    Multiply("×"),
    Divide("÷"),
    Plus("+"),
    Minus("-"),
    Reverse("(-"),
    ScopeStart("("),
    ScopeEnd(")"),
    Percentage("%")
}