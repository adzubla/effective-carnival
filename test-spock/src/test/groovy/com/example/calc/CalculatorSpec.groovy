package com.example.calc

import spock.lang.Specification

class CalculatorSpec extends Specification {

    def "Adicao"() {
        System.out.println("*** CalculatorSpec.Adicao");
        given:
            Calculator calculator = new Calculator()
        when:
            3 == calculator.add(1, 2)
        then:
            3 == calculator.add(2, 1)
    }

    def "Subtracao"() {
        System.out.println("*** CalculatorSpec.Subtracao");
        given:
            Calculator calculator = new Calculator()
        when:
            -1 == calculator.subtract(1, 2)
        then:
            1 == calculator.subtract(2, 1)
    }

}
