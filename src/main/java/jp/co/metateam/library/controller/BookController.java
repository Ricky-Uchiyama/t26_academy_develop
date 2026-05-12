package jp.co.metateam.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.service.BookMstService;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

/**
 * 書籍関連クラス
 */
@Log4j2
@Controller
public class BookController {

    private final BookMstService bookMstService;

    @Autowired
    public BookController(BookMstService bookMstService) {
        this.bookMstService = bookMstService;
    }

    @GetMapping("/book/index")
    public String index(Model model) {
        // 書籍を全件取得
        List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();

        model.addAttribute("bookMstList", bookMstList);

        return "book/index";
    }

    @GetMapping("/book/add")
    public String add(Model model) {
        if (!model.containsAttribute("bookMstDto")) {
            model.addAttribute("bookMstDto", new BookMstDto());
        }

        return "book/add";
    }

    @PostMapping("/book/add")
    public String create(@ModelAttribute BookMstDto bookMstDto, Model model) {
        try {
            Map<String, String> errorMap = this.bookMstService.validateForCreate(bookMstDto);
            if (!errorMap.isEmpty()) {
                model.addAttribute("bookMstDto", bookMstDto);
                model.addAttribute("errTitle", errorMap.get("errTitle"));
                model.addAttribute("errISBN", errorMap.get("errISBN"));
                return "book/add";
            }
            this.bookMstService.save(bookMstDto);
            return "redirect:/book/index";
        } catch (Exception e) {
            log.error("書籍登録処理でエラーが発生しました", e);
            model.addAttribute("bookMstDto", bookMstDto);
            return "redirect:/book/add";
        }
    }
}