package com.bolitik.id.controller;

import com.bolitik.id.entity.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {

    ArrayList products = new ArrayList(){{
        add(new Product("product1", 10));
        add(new Product("product2", 20));
        add(new Product("product3", 30));
        add(new Product("product4", 40));
    }};

    @GetMapping("/list")
    public ResponseEntity<List<Product>> list(){
        return new ResponseEntity(products, HttpStatus.OK);
    }
}
