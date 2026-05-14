package jp.co.metateam.library.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.micrometer.common.util.StringUtils;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.repository.BookMstRepository;

import java.util.Map;

@Service
public class BookMstService {

    private final BookMstRepository bookMstRepository;

    @Autowired
    public BookMstService(BookMstRepository bookMstRepository) {
        this.bookMstRepository = bookMstRepository;
    }

    public List<BookMstDto> findAvailableWithStockCount() {
        List<BookMst> books = this.bookMstRepository.findLimitedBook();
        List<BookMstDto> bookMstDtoList = new ArrayList<BookMstDto>();

        // 書籍の在庫数を取得
        // FIXME: 現状は書籍ID毎にDBに問い合わせている。一度のSQLで完了させたい。
        for (int i = 0; i < books.size(); i++) {
            BookMst book = books.get(i);
            BookMstDto bookMstDto = new BookMstDto();
            bookMstDto.setId(book.getId());
            bookMstDto.setIsbn(book.getIsbn());
            bookMstDto.setTitle(book.getTitle());
            bookMstDtoList.add(bookMstDto);
        }

        return bookMstDtoList;
    }

    public Map<String, String> validateForCreate(BookMstDto bookMstDto) {
        Map<String, String> errorMap = new HashMap<>();
        String title = bookMstDto.getTitle();
        String isbn = bookMstDto.getIsbn();
        if (title == null || title.isBlank()) {
            errorMap.put("errTitle", "書籍名は必須です");
        } else if (title.length() > 255) {
            errorMap.put("errTitle", "書籍名は255文字以内で入力してください");
        }
        if (isbn == null || isbn.isBlank()) {
            errorMap.put("errISBN", "ISBNは必須です");
        } else if (isbn.length() > 13) {
            errorMap.put("errISBN", "ISBNは13文字以内で入力してください");
        } else if (!isbn.matches("^[0-9]+$")) {
            errorMap.put("errISBN", "ISBNは半角数字で入力してください");

        } else if (this.bookMstRepository.findByIsbn(isbn).isPresent()) {
            errorMap.put("errISBN", "そのISBNは重複しています");
        }
        return errorMap;
    }

    @Transactional
    public void save(BookMstDto bookMstDto) {
        BookMst bookMst = new BookMst();
        bookMst.setTitle(bookMstDto.getTitle());
        bookMst.setIsbn(bookMstDto.getIsbn());
        this.bookMstRepository.save(bookMst);
    }
}