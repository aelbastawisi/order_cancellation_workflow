package com.camunda.workflow.domain;

import java.io.Serializable;
import java.util.UUID;

public class Order implements Serializable {
    private String id;
    private String name;
    private String address;

    public Order() {
    }

    public static Order createDummyOrder(){
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setName("Order: "+order.getId());
        order.setAddress("Address: "+order.getId());
        return order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
