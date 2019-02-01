package moor.example.testmoor

class CalculatorTest extends spock.lang.Specification {
    def "Calculate"() {
        expect:
        result == Calculator.calculate(expression)
        where:
        result                                     | expression
        "16"                                       | "(4 + 4) * 2"
        "5"                                        | "5"
        "Неверное выражение. Расчету не подлежит." | "5a"
        "Неверное выражение. Расчету не подлежит." | ""
        "Выражение содержит неверный символ"       | "a+2"
        "6.6"                                      | "2.2*3"
        "2.2"                                      | "2,2"
        "27.5"                                     | " (2 + 9 /(6-3))*5.5"
        "Неверное выражение. Расчету не подлежит." | "(2 + 9 / (6  -3 ) )5.5"

    }
}
