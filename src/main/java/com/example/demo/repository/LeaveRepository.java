package com.example.demo.repository;

import com.example.demo.model.Leave;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Integer> {
    List<Leave> findByUser(User user);
    List<Leave> findByStatus(String status);
}
