package com.sainsbury.challenge;

import com.sainsbury.challenge.pojo.Product;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class SolutionTest {

    @Test
    public void parseProductPage() throws IOException {
        Helper helper = new Helper();
        Solution solution = new Solution();
        String html = helper.readFile("com/sainsbury/challenge/data/avocado.html");
        Product product = solution.parseProductPage(html);

        Assert.assertEquals("Sainsbury's Avocado, Ripe & Ready x2", product.getTitle());
        Assert.assertEquals(1.80, product.getUnit_price(), 0.0001f);
        Assert.assertEquals("Avocados", product.getDescription());
        Assert.assertEquals("73.61kb", product.getSize());
    }

    @Test
    public void parseResultsPage() throws IOException {
        Helper helper = new Helper();
        Solution solution = new Solution();
        String html = helper.readFile("com/sainsbury/challenge/data/sainsburys.html");
        List<String> urls = solution.parseResultsPage(html);
        Iterator<String> iterator = urls.iterator();

        String first = iterator.next();
        String expected =
                "https://www.sainsburys.co.uk/shop/gb/groceries/ripe---ready/sainsburys-avocado--ripe---ready-x2";
        Assert.assertEquals(expected, first);

        String second = iterator.next();
        expected = "https://www.sainsburys.co.uk/shop/gb/groceries/ripe---ready/sainsburys-conference-pears--ripe---ready-x4-%28minimum%29";
        Assert.assertEquals(expected, second);
    }
}