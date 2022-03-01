package com.ttasjwi.servlet.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MemberRepositoryTest {

    MemberRepository memberRepository = MemberRepository.getInstance();

    @AfterEach
    void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    @DisplayName("저장한 멤버에 대해, 같은 아이디로 찾았을 때 같은 회원이 반환되어야한다.")
    void save() throws Exception {
        //given
        Member member = new Member("hello", 20);

        //when
        Member savedMember = memberRepository.save(member);

        //then
        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    @DisplayName("findAll 메서드 호출 시 모든 멤버들이 저장된 리스트가 반환되어야한다.")
    void findAll() {
        //given
        Member member1 = new Member("member1", 20);
        Member member2 = new Member("member2", 30);

        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> result = memberRepository.findAll();

        //then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(result.size()).isEqualTo(2);
        softAssertions.assertThat(result).contains(member1, member2);
        softAssertions.assertAll();
    }

}