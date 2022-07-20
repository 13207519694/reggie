package com.yanhao.reggie.dto;

import com.yanhao.reggie.entity.OrderDetail;
import com.yanhao.reggie.entity.Orders;
import com.yanhao.reggie.entity.OrderDetail;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
