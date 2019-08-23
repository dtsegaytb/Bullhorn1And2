package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    CloudinaryConfig cloudc;

    @Autowired
    ItemsRepository itemRepository;

    @RequestMapping("/")
    public String listItems(Model model){
        model.addAttribute("items", itemRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String itemForm(Model model){
        model.addAttribute("item", new Item());
        return "itemform";
    }

    @PostMapping("/process")
    public String processForm(@Valid Item item, BindingResult result) {
        if (result.hasErrors()) {
            return "itemform";
        }
    itemRepository.save(item);
        return "redirect:/";
    }

    @PostMapping("/add")
    public String processActor(@ModelAttribute Item item,
                               @RequestParam("file") MultipartFile file){
        if (file.isEmpty()){
            return "redirect:/add";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            item.setHeadShot(uploadResult.get("url").toString());
            itemRepository.save(item);
        } catch (IOException e) {
            e.printStackTrace();
            return "rediderct:/add";
        }
        return "redirect:/";
    }
    @RequestMapping("/detail/{id}")
    public String showItem(@PathVariable("id") long id, Model model){
        model.addAttribute("item", itemRepository.findById(id).get());
        return "show";
    }
    @RequestMapping("/update/{id}")
    public String updatItem(@PathVariable("id") long id, Model model){
        model.addAttribute("item", itemRepository.findById(id).get());
        return "itemform";
    }
}
