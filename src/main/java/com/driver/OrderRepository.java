package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String, Order> orderMap;
    HashMap<String, DeliveryPartner> partnerMap;
    HashMap<String, String> orderPartnerMap;
    HashMap<String, List<String>> partnerOrderListMap ;

    public OrderRepository() {
        this.orderMap = new HashMap<>();
        this.partnerMap = new HashMap<>();
        this.orderPartnerMap = new HashMap<>();
        this.partnerOrderListMap = new HashMap<>();
    }

    public void addOrder(Order order) {
        orderMap.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
        partnerMap.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        orderPartnerMap.put(orderId, partnerId);
        DeliveryPartner partner= partnerMap.get(partnerId);
        partner.setNumberOfOrders(partner.getNumberOfOrders()+1);
        List<String> orderList = new ArrayList<>();
        if (partnerOrderListMap.containsKey(partnerId)) {
            orderList = partnerOrderListMap.get(partnerId);
        }
        orderList.add(orderId);
        partnerOrderListMap.put(partnerId, orderList);
    }

    public Order getOrderById(String orderId) {
        if (orderMap.containsKey(orderId)) return orderMap.get(orderId);
        return null;
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        if (partnerMap.containsKey(partnerId)) return partnerMap.get(partnerId);
        return null;
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
       return partnerMap.get(partnerId).getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        return partnerOrderListMap.get(partnerId);
    }

    public List<String> getAllOrders() {
        return new ArrayList<>(orderMap.keySet());
    }

    public Integer getCountOfUnassignedOrders() {
        return orderMap.size() - orderPartnerMap.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        Integer count = 0;
        Integer delTime = (Integer.parseInt(time.substring(0, 2))) * 60 + (Integer.parseInt(time.substring(3)));
        List<String> orderList = partnerOrderListMap.get(partnerId);
        for (String order : orderList) {
            if (orderMap.get(order).getDeliveryTime() > delTime) count++;
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        Integer maxTime=0;
            List<String> orderList = partnerOrderListMap.get(partnerId);
            for(String order:orderList){
                if(maxTime<orderMap.get(order).getDeliveryTime()) maxTime=orderMap.get(order).getDeliveryTime();
            }
        Integer hours=maxTime/60;
            Integer minutes= maxTime-(hours*60);
            if(hours<10) return "0"+hours+":"+minutes;
        return hours +":"+ minutes;
    }

    public void deletePartnerById(String partnerId) {
        List<String> orderList=partnerOrderListMap.get(partnerId);
        for(String order:orderList){
            orderPartnerMap.remove(order);
        }
        partnerOrderListMap.remove(partnerId);
        partnerMap.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {

        //get partner id from order Partner Pair map
        String partnerId=orderPartnerMap.get(orderId);

        //remove the order from assigned order for a partner
        List<String> orderList;
        orderList=partnerOrderListMap.get(partnerId);
        orderList.remove(orderId);
        orderPartnerMap.remove(orderId);
        orderMap.remove(orderId);
        //decrease the number of orders from that partner
        if(partnerMap.containsKey(partnerId)) {
            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(partner.getNumberOfOrders() - 1);
        }
    }
}
