package com.tenco.blog.purchase;

import com.tenco.blog.board.Board;
import com.tenco.blog.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

/**
 * 구매 내역 엔티티
 * User 와 Board 의 구매 이력 관계를 표현 함
 * <p>
 * 한 사람의 사용자는 여러 게시글을 구매할 수 있다.
 * 한 게시글은 여러 사용자에게 구매될 수 있다.
 * User : Board - 다대다 관계로 표현이 되기 떄문에
 * 중간 테이블 (Purchase) 이 생성이 되어야 한다.
 * Purchase : User --> @Many to one -- join column 이름 지정
 * Purchase : board --> @Many to one -- join column 이름 지정
 */

@Data
@NoArgsConstructor
@Table(name = "purchase_tb",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_board", columnNames = {"user_id", "board_id"})
        })
@Entity
public class Purchase {
    // 복합키..DB 물리적 구조 유니크 설정이 필요
    // id 추가
    // 누가 구매를 했는지 정보 저장
    // 어떤 게시글을 구매했는지 정보 저장
    // 게시글 구매 금액(500p 고정) 지불한 포인트 이력 관리
    // 언제 구매했는지 시간이 필요

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 단방향 관계 "Purchase -> User"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;


    // 구매시 지불한 포인트
    private Integer price;



    @CreationTimestamp // pc -> db 자동주입
    private Timestamp createdAt;



    @Builder
    public Purchase(User user, Board board, Integer price) {
        this.user = user;
        this.board = board;
        this.price = price;
    }
}
