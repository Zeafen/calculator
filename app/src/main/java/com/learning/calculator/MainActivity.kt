package com.learning.calculator

import android.app.Notification.Action
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.learning.calculator.databinding.ActivityMainBinding
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var variables : MutableList<Double>
    private lateinit var operations : MutableList<String>
    private lateinit var formula : String
    private var result : Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        variables = mutableListOf()
        operations = mutableListOf()

        binding.apply {

            this.buttonClear.setOnClickListener {
                formula = ""
                operations.clear()
                variables.clear()
                result = null
                this.textViewFormula.text = formula
                this.textViewResult.text = ""
            }
            this.buttonDelete.setOnClickListener{
                formula.replaceRange(formula.lastIndex, formula.lastIndex+1, "")
            }

            this.buttonScope.setOnClickListener{
                if(formula.isEmpty()){
                    formula += "("
                    updateUi()
                    return@setOnClickListener
                }
                val scopeInd = formula.indexOfLast { ch -> ch == '(' }
                formula += when {
                    Actions.entries.any { a ->
                        a != Actions.Reverse && a != Actions.Percentage && a != Actions.ScopeEnd && a.actionTxt == formula.last()
                            .toString()
                    } -> "("

                    scopeInd != -1 &&
                            !Actions.entries.any { a ->
                                a.actionTxt == formula.last().toString()
                            } -> {
                        if (formula.count { ch -> ch == '(' } - formula.count { ch -> ch == ')' } >= 1) ")" else {
                            Toast.makeText(
                                applicationContext,
                                "Cannot add scope without leading scope",
                                Toast.LENGTH_SHORT
                            ).show()
                            ""
                        }
                    }

                    scopeInd != -1 &&
                            formula.last().toString() == ")" ->
                        if (formula.count { ch -> ch == '(' } - formula.count { ch -> ch == ')' } >= 1) ")"
                        else {
                            Toast.makeText(
                                applicationContext,
                                "Cannot add scope without leading scope",
                                Toast.LENGTH_SHORT
                            ).show()
                            ""
                        }

                    else -> {
                        Toast.makeText(
                            applicationContext,
                            "Cannot add scope without leading operation",
                            Toast.LENGTH_SHORT
                        ).show()
                        ""
                    }
                }
                updateUi()
            }

            this.buttonPercentage.setOnClickListener{
                if(Actions.entries.any{ a -> a.actionTxt == formula.last().toString() })
                    Toast.makeText(applicationContext, "Invalid operation", Toast.LENGTH_SHORT).show()
                else formula += "%"
                updateUi()
            }

            this.buttonDivide.setOnClickListener{
                updateOperations(Actions.Divide)
            }
            this.buttonMultiply.setOnClickListener{
                updateOperations(Actions.Multiply)
            }
            this.buttonPlus.setOnClickListener{
                updateOperations(Actions.Plus)
            }
            this.buttonMinus.setOnClickListener{
                updateOperations(Actions.Minus)
            }

            this.buttonResult.setOnClickListener {
                if(formula.isEmpty())
                    formula += "0"
                updateUi()
                this.buttonMultiply
                formula = result.toString()
                this.textViewFormula.text = formula
                variables.apply {
                    this.clear()
                    this.add(result ?: 0.0)
                }
                operations.clear()
            }

            this.buttonReverse.setOnClickListener {
                when {
                    formula.isEmpty() -> {
                        formula += "(-"
                        updateUi()
                        return@setOnClickListener
                    }
                    Actions.entries.any{ a -> a.actionTxt == formula.last().toString() } ->{
                        formula += "(-"
                    }

                    !Actions.entries.any { a -> a.actionTxt == formula.last().toString() } -> {
                        val actionInd =
                            formula.indexOfLast { ch -> Actions.entries.any { a -> a.actionTxt == ch.toString() } }
                        val number = formula.substring(actionInd + 1)
                        if (actionInd > 0 && formula.substring(
                                actionInd - 1,
                                actionInd + 1
                            ) == Actions.Reverse.actionTxt
                        ) {
                            formula =
                                formula.replaceRange(actionInd - 1, formula.lastIndex + 1, number)
                        } else {
                            formula = formula.replaceRange(
                                actionInd + 1,
                                actionInd + number.length,
                                "(-${number}"
                            )
                        }
                    }

                    formula.substring(formula.lastIndex - 1) == "(-" ->
                        formula =
                            formula.replaceRange(formula.lastIndex - 1, formula.lastIndex + 1, "")

                    else ->
                        Toast.makeText(applicationContext, "Invalid operation", Toast.LENGTH_SHORT)
                            .show()

                }
                updateUi()
            }

            this.buttonOne.setOnClickListener{
                formula += "1"
                updateUi()
            }
            this.buttonTwo.setOnClickListener{
                formula += "2"
                updateUi()
            }
            this.buttonThree.setOnClickListener{
                formula += "3"
                updateUi()
            }
            this.buttonFour.setOnClickListener{
                formula += "4"
                updateUi()
            }
            this.buttonFive.setOnClickListener{
                formula += "5"
                updateUi()
            }
            this.buttonSix.setOnClickListener{
                formula += "6"
                updateUi()
            }
            this.buttonSeven.setOnClickListener{
                formula += "7"
                updateUi()
            }
            this.buttonEight.setOnClickListener{
                formula += "8"
                updateUi()
            }
            this.buttonNine.setOnClickListener{
                formula += "9"
                updateUi()
            }
            this.buttonZero.setOnClickListener{
                formula += "0"
                updateUi()
            }
            this.buttonDot.setOnClickListener{
                if(Actions.entries.any{ a -> a.actionTxt == formula.last().toString() })
                    formula += "0."
                else{
                    val actionInd = formula.indexOfLast { ch -> Actions.entries.any{ a -> a.actionTxt == ch.toString() } }
                    if(!formula.substring(actionInd+1).contains("."))
                        formula += "."
                    else
                        Toast.makeText(applicationContext, "Invalid operation", Toast.LENGTH_SHORT).show()
                }
                updateUi()
            }
        }
        updateUi()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("formula", formula)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        formula = savedInstanceState.getString("formula") ?: "0.0"
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun updateOperations(operation : Actions) {
        if (formula.isEmpty())
            Toast.makeText(applicationContext, "Invalid operation", Toast.LENGTH_SHORT).show()
        if (Actions.entries.any { a ->
                a != Actions.ScopeEnd && a != Actions.Percentage &&
                        a.actionTxt == formula.last().toString()
            })
            formula = formula.replaceRange(formula.lastIndex, formula.lastIndex + 1, operation.actionTxt)
        else
            formula += operation.actionTxt
        updateUi()
    }

    private fun updateUi(){
        binding.apply {
            this.textViewFormula.text = formula
            if(formula.isNotEmpty()) {
                if (Actions.entries.any { a ->
                        a != Actions.ScopeEnd && a != Actions.Percentage && a.actionTxt == formula.last()
                            .toString()
                    }) {
                    this.textViewResult.text = ""
                    return
                }
                try {
                    observeFormula(formula)
                    result = processOperations(operations, variables)
                    this.textViewResult.text = result?.toString() ?: "Finish the formula"
                } catch (ex: Exception) {
                    Toast.makeText(applicationContext, ex.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun processOperations(operations : List<String>, variables : List<Double>) : Double {
        val operationsM = operations.toMutableList()
        val variablesM = variables.toMutableList()

        while (operationsM.contains("(")) {
            val scopeInd = operationsM.indexOfFirst { ch -> ch == "(" }
            if (operationsM.count { ch -> ch == "(" } - operations.count { ch -> ch == ")" } >= 2
                && operationsM.count{ ch -> ch == "(" } > 1)
                throw Exception("Couldn't find enough amount of ')'")
            var endScope: Int = -1
            if (operationsM.contains(")")) {
                var scopeCount = operationsM.subList(scopeInd + 1, operationsM.indexOfFirst { ch -> ch == ")" })
                    .count { ch -> ch == "(" }
                if (scopeCount >= 1) {
                    for (i in operationsM.indexOfFirst { ch -> ch == ")" }..operationsM.indexOfLast { ch -> ch == ")" }) {
                        if (scopeCount > 0 && operationsM[i] == ")")
                            scopeCount -= 1
                        else
                            if (operationsM[i] == ")") {
                                endScope = i
                                break
                            }
                    }
                    if (endScope == -1 && scopeCount <= 1)
                        endScope = operationsM.lastIndex + 1
                } else
                    endScope = operationsM.indexOfFirst { ch -> ch == ")" }
            } else
                endScope = operationsM.lastIndex + 1

            val endVarScope = if (endScope > variablesM.lastIndex + 1) variablesM.lastIndex + 1 else endScope
            val scopeRes =
                processOperations(operationsM.subList(scopeInd + 1, endScope), variablesM.subList(scopeInd, endVarScope))
            for (i in scopeInd..endScope)
                if(scopeInd < operationsM.count())
                    operationsM.removeAt(scopeInd)
            for (i in scopeInd..<endVarScope)
                if(scopeInd < variablesM.count())
                    variablesM.removeAt(scopeInd)
            variablesM.add(scopeInd, scopeRes)

        }
        while (operationsM.isNotEmpty()) {
            var scopeRes: Double
            while (operationsM.any { ch -> ch == Actions.Divide.actionTxt
                        || ch == Actions.Multiply.actionTxt
                        || ch == "^" ||
                        ch == "%" }) {
                val divI = operationsM.indexOf(Actions.Divide.actionTxt)
                val multI = operationsM.indexOf(Actions.Multiply.actionTxt)
                val powI = operationsM.indexOf("^")
                when {
                    divI != -1 && ((divI < multI && divI < powI && (powI != -1 && multI != -1))
                            || (powI == -1 && multI == -1)
                            || (divI < powI && multI == -1)
                            || (divI < multI && powI == -1)) -> {
                        if (variablesM[divI + 1] == 0.0)
                            throw IllegalArgumentException("Cannot divide by Zero")
                        scopeRes = variablesM[divI] / variablesM[divI + 1]
                        operationsM.removeAt(divI)
                        for (i in divI..divI + 1)
                            variablesM.removeAt(divI)
                        variablesM.add(divI, scopeRes)
                    }

                    multI != -1 && ((multI < powI && multI < divI && (powI != -1 && divI != -1))
                            || (divI == -1 && powI == -1)
                            || (multI < divI && powI == -1)
                            || (multI < powI && divI == -1)) -> {
                        scopeRes = variablesM[multI] * variablesM[multI + 1]
                        operationsM.removeAt(multI)
                        for (i in multI..multI + 1)
                            variablesM.removeAt(multI)
                        variablesM.add(multI, scopeRes)
                    }
                    powI != -1 && ((powI < multI && powI < divI && (multI != -1 && divI != -1))
                            || (divI == -1 && multI == -1)
                            || (powI < divI && multI == -1)
                            || (powI < multI && divI == -1)) -> {
                        scopeRes = variablesM[powI].pow(variablesM[powI + 1])
                        operationsM.removeAt(powI)
                        for (i in powI..powI + 1)
                            variablesM.removeAt(powI)
                        variablesM.add(powI, scopeRes)
                    }
                }
            }
            if (operationsM.isEmpty())
                break
            val plusI = operationsM.indexOf("+")
            val minusI = operationsM.indexOf("-")
            if (plusI < minusI && plusI != -1 && minusI != -1 || plusI != -1 && minusI == -1) {
                scopeRes = variablesM[plusI] + variablesM[plusI + 1]
                operationsM.removeAt(plusI)
                for (i in plusI..plusI + 1)
                    variablesM.removeAt(plusI)
                variablesM.add(plusI, scopeRes)
            }
            if(minusI < plusI && plusI != -1 && minusI != -1 || minusI != -1 && plusI == -1) {
                scopeRes = variablesM[minusI] - variablesM[minusI + 1]
                operationsM.removeAt(minusI)
                for (i in minusI..minusI + 1)
                    variablesM.removeAt(minusI)
                variablesM.add(minusI, scopeRes)
            }
        }
        return variablesM.first()
    }

    private fun observeFormula(formula : String){
        operations.clear()
        variables.clear()
        var index = 0
        var number = ""
        while(index < formula.length){
            when{
                index<formula.length-1 && formula.substring(index, index+2) == Actions.Reverse.actionTxt -> {
                    operations.add("(")
                    index+=2
                    number +="-"
                    continue
                }
                Actions.entries.any { a -> a.actionTxt == formula[index].toString() } -> {
                    if(formula[index] == '%' && Actions.entries.any { a -> a.actionTxt != formula[index-1].toString() }){
                        variables[variables.lastIndex] = variables.last() / 100
                        ++index
                        continue
                    }
                    operations.add(formula[index].toString())
                    ++index
                    continue
                }
                else -> {
                    while (index < formula.length &&
                        !Actions.entries.any { a -> a.actionTxt == formula[index].toString() }) {
                        number += formula[index]
                        ++index
                    }
                    variables.add(number.toDouble())
                    number = ""
                }
            }
        }
    }
}