package com.yanhao.reggie.dto;

import com.yanhao.reggie.entity.Setmeal;
import com.yanhao.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
