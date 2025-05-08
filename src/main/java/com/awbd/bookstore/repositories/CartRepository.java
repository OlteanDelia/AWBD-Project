package com.awbd.bookstore.repositories;

import com.awbd.bookstore.models.Cart;
import com.awbd.bookstore.models.User;
import com.awbd.bookstore.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long userId);

    boolean existsByUserId(Long userId);


    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Cart c JOIN c.books b WHERE c.id = :cartId AND b.id = :bookId")
    boolean existsBookInCart(@Param("cartId") Long cartId, @Param("bookId") Long bookId);


    @Query("SELECT SUM(b.price) FROM Cart c JOIN c.books b WHERE c.id = :cartId")
    double calculateTotalPrice(@Param("cartId") Long cartId);



    void deleteByUser(User user);

}
