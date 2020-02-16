package at.pflegerprogramming.simplecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    var onlyNumbersAdded: Boolean = true
    var lastInputIsANumber: Boolean = false
    var neverCalculated:Boolean = true
    var buttonAlreadyPressed:Boolean = false
    var lastEventWasEqual:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Values
        tv_zero.setOnClickListener{changeCalculatorInput("0",false)}
        tv_one.setOnClickListener{changeCalculatorInput("1",false)}
        tv_two.setOnClickListener{changeCalculatorInput("2",false)}
        tv_three.setOnClickListener{changeCalculatorInput("3",false)}
        tv_four.setOnClickListener{changeCalculatorInput("4",false)}
        tv_five.setOnClickListener{changeCalculatorInput("5",false)}
        tv_six.setOnClickListener{changeCalculatorInput("6",false)}
        tv_seven.setOnClickListener{changeCalculatorInput("7",false)}
        tv_eight.setOnClickListener{changeCalculatorInput("8",false)}
        tv_nine.setOnClickListener{changeCalculatorInput("9",false)}
        tv_comma.setOnClickListener{
            val indexOfLastChar = getLastCharInString()
            val inputString = tv_calculatorInput.text.toString()
            if (inputString.takeLast(1) != "."){
                if(indexOfLastChar != -1 && !(inputString.substring(indexOfLastChar,inputString.length-1).contains("."))){
                    changeCalculatorInput(".",false)
                }else if(indexOfLastChar == -1){
                    changeCalculatorInput(".",false)
                }
            }


        }

        //Operator
        tv_plus.setOnClickListener{changeCalculatorInput("+",true)}
        tv_minus.setOnClickListener{changeCalculatorInput("-",true)}
        tv_multiply.setOnClickListener{changeCalculatorInput("*",true)}
        tv_divide.setOnClickListener{changeCalculatorInput("/",true)}
        tv_brackets.setOnClickListener{
            val openBracketCount = counter('(')
            val closedBracketCount = counter(')')

            if(openBracketCount == closedBracketCount){
                tv_calculatorInput.append("(")
            }else{
                tv_calculatorInput.append(")")
            }


            calculateExpression()
        }
        tv_clearAll.setOnClickListener{
            clearEvent()
        }
        tv_delete.setOnClickListener{
            val inputAsString = tv_calculatorInput.text.toString()
            if(inputAsString.isNotEmpty()) {
                tv_calculatorInput.text = inputAsString.substring(0, inputAsString.length - 1)
                calculateExpression()

                if (neverCalculated) {
                    tv_calculatorSolution.text = ""
                }
                try {
                    val number = tv_calculatorInput.text.toString().takeLast(1).toInt()
                } catch (e: Exception) {
                    tv_calculatorSolution.text = ""
                }
            }
        }
        tv_equals.setOnClickListener{
            val solutionString = tv_calculatorSolution.text.toString()
            if(solutionString.isNotEmpty() && solutionString != "Divided by 0"){
                if(solutionString.length > 12 && solutionString.indexOf('E') != -1){
                    //remove in the middle to a maximum of E-XXX or EXXXX
                    val eSubstring = solutionString.substring(0,7) + solutionString.substring(solutionString.indexOf('E'))
                    tv_calculatorSolution.text = eSubstring

                }else if(solutionString.length > 12 && solutionString.indexOf('.') != -1){
                    //is comma separated -> string can be shortened
                    tv_calculatorSolution.text = solutionString.substring(0,12)

                }else if(solutionString.length > 12){
                    //convert to E-number if longer then 12 and no comma/E
                    val shortSolutionString = solutionString.substring(0,1) + "." + solutionString.substring(1,7) + "E" + (solutionString.length -1).toString()
                    tv_calculatorSolution.text = shortSolutionString
                }
                tv_calculatorInput.text = tv_calculatorSolution.text
                tv_calculatorSolution.text = ""
                resetParameters()
                lastEventWasEqual = true
            }
        }


    }

    fun changeCalculatorInput(valueParameter: String, inputIsNoNumber: Boolean){
        val inputString = tv_calculatorInput.text.toString()
        if(lastEventWasEqual && !(inputIsNoNumber)){
            clearEvent()
        }else if(lastEventWasEqual){
            lastEventWasEqual = false
        }

        if(onlyNumbersAdded){
            tv_calculatorInput.append(valueParameter)
            if(inputIsNoNumber){
                onlyNumbersAdded = false
            }
        }else{
            if(inputIsNoNumber){
                try{
                    val number = inputString.takeLast(1).toInt()
                }catch (e:Exception){
                    lastInputIsANumber = inputString.takeLast(1) == "(" || inputString.takeLast(1) == ")"
                }
            }else{
                lastInputIsANumber = true
            }

            if(lastInputIsANumber){
                tv_calculatorInput.append(valueParameter)
                calculateExpression()
            }
        }
    }

    fun calculateExpression(){
        try {
            if (tv_calculatorInput.text.toString().takeLast(2) == "/0"){
                tv_calculatorSolution.text = "Divided by 0"
            }else{
                val expression = ExpressionBuilder(tv_calculatorInput.text.toString()).build()
                val result = expression.evaluate()
                val longResult = result.toLong()
                if(result == longResult.toDouble()){
                    tv_calculatorSolution.text = longResult.toString()
                }else{
                    tv_calculatorSolution.text = result.toString()
                }

                neverCalculated = tv_calculatorInput.text.toString().length == tv_calculatorSolution.text.toString().length

            }


        }catch (e:Exception){

        }
    }

    fun resetParameters(){
        lastInputIsANumber = false
        onlyNumbersAdded = true
        neverCalculated = true
        buttonAlreadyPressed = false
    }

    fun counter(searchChar:Char): Int{
        var charCount =0
        val searchString = tv_calculatorInput.text.toString()

        for(element in searchString){
            if(searchChar == element){
                ++charCount
            }
        }

        return charCount
    }

    fun getLastCharInString():Int{
        val inputString = tv_calculatorInput.text.toString()
        var indexOfLastCharInString:Int = -1

        if (inputString.isNotEmpty()){
            var indexCounter = 0
            for(i in 1..inputString.length){
                ++indexCounter
                try {
                    val number = inputString.substring(0,i).takeLast(1).toInt()
                }catch (e: Exception){
                    indexOfLastCharInString = i-1
                }
            }
        }
        return indexOfLastCharInString

    }

    fun clearEvent(){
        tv_calculatorInput.text = ""
        tv_calculatorSolution.text = ""
        resetParameters()
        lastEventWasEqual = false
    }
}
