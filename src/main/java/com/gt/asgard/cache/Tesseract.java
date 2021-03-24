package com.gt.asgard.cache;

import com.gt.asgard.enums.BookType;
import com.gt.common.view.OrderView;

import java.util.*;

public class Tesseract implements InfinityStone {

    // These 4 must always be affected together
    private double amountAvailable;
    private TreeMap<Double, Long> numberOfSharesPerPrice;
    private Map<Double, Map<Long, OrderView>> ordersGroupedByPrice;
    private Map<Long, OrderView> cache;

    // This one may not
    private List<OrderView> completedOrders;

    public Tesseract(BookType bookType) {
        this.amountAvailable = 0;
        this.ordersGroupedByPrice = new HashMap<>();
        this.completedOrders = new ArrayList<>();
        this.cache = new HashMap<>();

        if (bookType == BookType.BID) {
            this.numberOfSharesPerPrice = new TreeMap<>(Collections.reverseOrder());
        } else {
            this.numberOfSharesPerPrice = new TreeMap<>();
        }
    }

    @Override
    public void add(OrderView order) throws Exception {
        long start = System.nanoTime();
        long id = order.getId();
        double price = order.getPrice();
        long quantity = order.getQuantity();

        if (cache.containsKey(id)) {
            throw new Exception("Order already exists. Do not use add -- use update");
        }

        if (ordersGroupedByPrice.get(price) != null && ordersGroupedByPrice.get(price).containsKey(id)) {
            throw new Exception("Order already exists. Do not use add -- use update");
        }

        ordersGroupedByPrice.computeIfAbsent(price, k -> new HashMap<>());

        cache.put(order.getId(), order);
        numberOfSharesPerPrice.put(order.getPrice(), numberOfSharesPerPrice.getOrDefault(order.getPrice(), 0L) + quantity);
        ordersGroupedByPrice.get(order.getPrice()).put(order.getId(), order);
        amountAvailable += quantity;
        long end = System.nanoTime();
        double seconds = (double) (end - start) / 1_000_000_000.0;
        if (id > 999_999 && id % 100_000 == 0) {
            System.out.println("Add to cache time: " + seconds);
        }
    }

    @Override
    public void remove(long orderID) throws Exception {
        long start = System.nanoTime();
        if (!cache.containsKey(orderID)) {
            throw new Exception("Order does not exist, therefore it cannot be deleted");
        }

        OrderView order = find(orderID);

        double price = order.getPrice();
        long quantityToRemove = order.getQuantityRemaining();

        order.setQuantityRemaining(0L);

        cache.remove(orderID);
        ordersGroupedByPrice.get(price).remove(orderID);
        numberOfSharesPerPrice.put(price, numberOfSharesPerPrice.get(price) - quantityToRemove);
        amountAvailable -= quantityToRemove;

        completedOrders.add(order);

        // Clean up -- remove keys that have no values
        if (numberOfSharesPerPrice.get(price).equals(0L)) {
            numberOfSharesPerPrice.remove(price);
        }

        if (ordersGroupedByPrice.get(price).size() == 0) {
            ordersGroupedByPrice.remove(price);
        }

        long end = System.nanoTime();
        double seconds = (double) (end - start) / 1_000_000_000.0;
        if (orderID > 999_999 && orderID % 100_000 == 0) {
            System.out.println("Remove from cache time: " + seconds);
        }
    }

    @Override
    public void update(OrderView newOrder, long quantityChanged) throws Exception {
        long start = System.nanoTime();
        OrderView cacheOrder = find(newOrder.getId());

        double cacheOrderPrice = newOrder.getPrice();

        numberOfSharesPerPrice.put(cacheOrderPrice, numberOfSharesPerPrice.get(cacheOrderPrice) - quantityChanged);
        ordersGroupedByPrice.get(cacheOrderPrice).put(cacheOrder.getId(), newOrder);
        cache.put(cacheOrder.getId(), newOrder);
        amountAvailable -= quantityChanged;
        long end = System.nanoTime();
        double seconds = (double) (end - start) / 1_000_000_000.0;
        if (cacheOrder.getId() > 999_999 && cacheOrder.getId() % 100_000 == 0) {
            System.out.println("Update from cache time: " + seconds);
        }
    }

    @Override
    public OrderView find(long orderID) throws Exception {
        long start = System.nanoTime();
        if (!cache.containsKey(orderID)) {
            throw new Exception("OrderID does not exist. Please check if valid orderID");
        }

        double price = cache.get(orderID).getPrice();
        OrderView output = ordersGroupedByPrice.get(price).get(orderID);

        long end = System.nanoTime();
        double seconds = (double) (end - start) / 1_000_000_000.0;
        if (orderID > 999_999 && orderID % 100_000 == 0) {
            System.out.println("Find from cache time: " + seconds);
        }

        return output;
    }

    public String listAvailablePerPrice() {
        StringBuilder output = new StringBuilder();
        for (double price : getNumberOfSharesPerPrice().keySet()) {
            long amount = getNumberOfSharesPerPrice().get(price);
            output.append("[").append(price).append(" -> ").append(amount).append("],");
        }
        return output.toString();
    }

    public String listOrdersPerPrice() {
        StringBuilder output = new StringBuilder();
        for (double price : getOrdersGroupedByPrice().keySet()) {
            int size = getOrdersGroupedByPrice().get(price).size();
            output.append("[").append(price).append(" -> ").append(size).append("], ");
        }
        return output.toString();
    }

    public String listCompletedOrders() {
        StringBuilder output = new StringBuilder();
        for (OrderView order : getCompletedOrders()) {
            output.append("\n").append(order.toString());
        }
        return output.toString();
    }

    public double getAmountAvailable() {
        return amountAvailable;
    }

    public void setAmountAvailable(double amountAvailable) {
        this.amountAvailable = amountAvailable;
    }

    public TreeMap<Double, Long> getNumberOfSharesPerPrice() {
        return numberOfSharesPerPrice;
    }

    public void setNumberOfSharesPerPrice(TreeMap<Double, Long> numberOfSharesPerPrice) {
        this.numberOfSharesPerPrice = numberOfSharesPerPrice;
    }

    public Map<Double, Map<Long, OrderView>> getOrdersGroupedByPrice() {
        return ordersGroupedByPrice;
    }

    public void setOrdersGroupedByPrice(Map<Double, Map<Long, OrderView>> ordersGroupedByPrice) {
        this.ordersGroupedByPrice = ordersGroupedByPrice;
    }

    public Map<Long, OrderView> getCache() {
        return cache;
    }

    public void setCache(Map<Long, OrderView> cache) {
        this.cache = cache;
    }

    public List<OrderView> getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(List<OrderView> completedOrders) {
        this.completedOrders = completedOrders;
    }

}
