package com.sainsbury.challenge.pojo;

import java.util.ArrayList;
import java.util.List;

public class Results {

    List<Product> results;
    double total;

    public Results() {
        this.results = new ArrayList<Product>();
    }

    public List<Product> getResults() {
        return results;
    }

    public void setResults(List<Product> results) {
        this.results = results;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
