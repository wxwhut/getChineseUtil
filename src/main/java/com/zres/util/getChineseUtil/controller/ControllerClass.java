package com.zres.util.getChineseUtil.controller;

import com.zres.util.getChineseUtil.bean.Setting;

import com.zres.util.getChineseUtil.getChineseUtilApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by wang xu on 2018/6/29.
 */

@Controller
public class ControllerClass {
    @PostMapping("/index")
    public static void start(@ModelAttribute Setting setting){
        getChineseUtilApplication.start(setting);
    }

    @GetMapping("/index")
    public static void index(Model model){
        model.addAttribute("setting", new Setting());
    }

}
