package com.yanhao.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanhao.reggie.common.R;
import com.yanhao.reggie.dto.DishDto;
import com.yanhao.reggie.entity.Category;
import com.yanhao.reggie.entity.Dish;
import com.yanhao.reggie.entity.DishFlavor;
import com.yanhao.reggie.entity.Employee;
import com.yanhao.reggie.service.CategoryService;
import com.yanhao.reggie.service.DishFlavorService;
import com.yanhao.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yanhao
 * @note
 * @create 2022-07-14 下午 4:55
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name)
                .orderByDesc(Dish::getUpdateTime);
        pageInfo = dishService.page(pageInfo,lambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtoPageRecords = records.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
//        List<DishDto> dishDtoPageRecords = null;
//        for (Dish dish : records){
//            DishDto dishDto = new DishDto();
//            BeanUtils.copyProperties(dish,dishDto);
//            Long categoryId = dish.getCategoryId();
//            Category category = categoryService.getById(categoryId);
//            if (category != null){
//                String categoryName = category.getName();
//                dishDto.setCategoryName(categoryName);
//            }
//            dishDtoPageRecords.add(dishDto);   //add有问题，报空指针异常
//        }
        dishDtoPage.setRecords(dishDtoPageRecords);

        return R.success(dishDtoPage);
    }

    /**
     * 修改菜品信息时查询展示数据
     * http://localhost:8081/dish/1547549477635489794
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        log.info("根据id查询菜品信息...");
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        if(dishDto != null){
            return R.success(dishDto);
        }
        return R.error("没有查询到菜品信息");
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        //清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        //清理某个分类下的缓存
        String key = "dish" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }

    /**
     * 删除菜品/批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String ids){
        //ids传的是字符串，没办法直接赋值List集合
//        dishService.removeByIdsWithFlavor(ids);

        String[] split = ids.split(",");
        for(String item : split){
            Long dishId = Long.parseLong(item);
            dishService.removeByIdWithFlavor(dishId);
            log.info(dishId.toString());
        }

        return R.success("删除菜品成功");
    }

    /**
     * 停售菜品/批量停售菜品  启售菜品/批量启售菜品
     * @param ids
     * @return
     */
    // 这里该是 /* 还是 /**  ？？？
    @PostMapping("/status/*")
    public R<String> statusChange(HttpServletRequest request, String ids){
        String requestURI = request.getRequestURI();
        String statusStr = requestURI.substring(requestURI.lastIndexOf("/")+1);
        int status = Integer.parseInt(statusStr);
        String[] split = ids.split(",");

        for(String item : split){
            Long id = Long.parseLong(item);
            LambdaUpdateWrapper<Dish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(Dish::getStatus,status).eq(Dish::getId,id);
            boolean update = dishService.update(lambdaUpdateWrapper);
            log.info(id.toString() + update);

            //清理某个分类下的缓存
            //Dish dish = dishService.getById(id);
            //String key = "dish" + dish.getCategoryId() + "_1";
            //redisTemplate.delete(key);
        }

        //清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return R.success((status == 0) ? "停售菜品成功" : "启售菜品成功");
    }

//    /**
//     * 停售菜品/批量停售菜品
//     * @param ids
//     * @return
//     */
//    @PostMapping({"/status/0"})
//    public R<String> statusStop(HttpServletRequest request, String ids) {
//        log.info(request.getRequestURI());
//        String[] split = ids.split(",");
//        String[] var4 = split;
//        int var5 = split.length;
//
//        for(int var6 = 0; var6 < var5; ++var6) {
//            String item = var4[var6];
//            Long id = Long.parseLong(item);
//            LambdaUpdateWrapper<Dish> lambdaUpdateWrapper = new LambdaUpdateWrapper();
//            ((LambdaUpdateWrapper)lambdaUpdateWrapper.set(Dish::getStatus, 0)).eq(Dish::getId, id);
//            boolean update = this.dishService.update(lambdaUpdateWrapper);
//            log.info(id.toString() + update);
//        }
//
//        return R.success("停售菜品成功");
//    }
//
//    /**
//     * 启售菜品/批量启售菜品
//     * @param ids
//     * @return
//     */
//    @PostMapping("/status/1")
//    public R<String> statusStart(String ids){
//        String[] split = ids.split(",");
//        for(String item : split){
//            Long id = Long.parseLong(item);
//            LambdaUpdateWrapper<Dish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//            lambdaUpdateWrapper.set(Dish::getStatus,1).eq(Dish::getId,id);
//            boolean update = dishService.update(lambdaUpdateWrapper);
//            log.info(id.toString() + update);
//        }
//        return R.success("启售菜品成功");
//    }

    /**
     * 展示在新增套餐时的菜品list列表-------------------新增查询菜品口味信息，返回dishDto
     * http://localhost:8081/dish/list?categoryId=1397844263642378242
     * http://39.106.131.193:8081/dish/list?categoryId=1397844263642378242&status=1
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List> list(Dish dish){
        List<DishDto> dishDtoList = null;
        String key = "dish" + dish.getCategoryId() + "_" + dish.getStatus();
        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果存在直接返回，无需查数据库
        if(dishDtoList != null){
            return R.success(dishDtoList);
        }
        //如果不存在，需要查询数据库

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dish::getCategoryId,dish.getCategoryId()).orderByAsc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(lambdaQueryWrapper);

        dishDtoList = dishList.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Category category = categoryService.getById(dish.getCategoryId());
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapperDishFlavor = new LambdaQueryWrapper<>();
            lambdaQueryWrapperDishFlavor.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapperDishFlavor);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //将查询到的菜品数据缓存到redis中
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
