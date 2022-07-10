package org.example.udf;

import org.springframework.stereotype.Service;

@Service
public class CalculatorCore {
    public int add(int a, int b) {
        return a + b;
    }
}