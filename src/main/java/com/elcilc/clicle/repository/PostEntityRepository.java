package com.elcilc.clicle.repository;

import com.elcilc.clicle.model.entity.PostEntity;
import com.elcilc.clicle.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {

    Page<PostEntity> findAllByUserId(Integer userId, Pageable pageable);

}
