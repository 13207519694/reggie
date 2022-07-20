package com.yanhao.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yanhao.reggie.common.BaseContext;
import com.yanhao.reggie.common.R;
import com.yanhao.reggie.entity.ShoppingCart;
import com.yanhao.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author yanhao
 * @note
 * @create 2022-07-19 下午 4:53
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
            public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
                //设置用户id，指定当前是哪个用户的购物车数据
                Long currentId = BaseContext.getCurrentId();
                shoppingCart.setUserId(currentId);
                //查询当前菜品或者套餐是否在购物车中
                Long dishId = shoppingCart.getDishId();

                LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);

                if(dishId != null){
                    //添加到购物车中
                    lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }

        Long setmealId = shoppingCart.getSetmealId();

        if(setmealId != null){
            //添加到购物车中
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }
        //如果已经存在，就在原来数量基础上加一
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        if(one != null){
            //如果已经存在，就在原来基础上加一
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);
        }else{
            //如果不存在，新增一个
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        //如果不存在，则添加到购物车，数量默认就是一

        return R.success(one);
    }

    /**
     * 减去一项购物车商品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }

        Long setmealId = shoppingCart.getSetmealId();

        if(setmealId != null){
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        Integer number = one.getNumber();
        if(number > 1){
            one.setNumber(number - 1);
            shoppingCartService.updateById(one);
        }else{
            //表单要回显数据，所以先设置为0，再清空
            one.setNumber(number - 1);
//            shoppingCartService.updateById(one);
            shoppingCartService.remove(lambdaQueryWrapper);
        }

//        one = shoppingCartService.getOne(lambdaQueryWrapper);
        return R.success(one);
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    public R<List> list(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId()).orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success(("清空购物车成功！"));
    }
}
