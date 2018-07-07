package com.zres.util.getChineseUtil.controller;

import com.zres.util.getChineseUtil.bean.Setting;

import com.zres.util.getChineseUtil.getChineseUtilApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * Created by wang xu on 2018/6/29.
 */

@Controller
public class ControllerClass {
    @RequestMapping(value = "/index",method = RequestMethod.POST)
    @ResponseBody
    public static String start(@ModelAttribute Setting setting){
        return getChineseUtilApplication.start(setting);
    }

    @GetMapping("/index")
    public static void index(Model model){
        model.addAttribute("setting", new Setting());
    }

}
