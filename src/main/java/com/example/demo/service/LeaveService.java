package com.example.demo.service;

import com.example.demo.model.Leave;
import com.example.demo.model.User;
import com.example.demo.repository.LeaveRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private UserRepository userRepository;

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Leave applyLeave(Leave leave) {
        return leaveRepository.save(leave);
    }

    public List<Leave> getLeavesByUser(User user) {
        return leaveRepository.findByUser(user);
    }

    public List<Leave> getPendingLeaves() {
        return leaveRepository.findByStatus("PENDING");
    }

    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    public void updateLeaveStatus(int leaveId, String status) {
        Optional<Leave> leave = leaveRepository.findById(leaveId);
        leave.ifPresent(l -> {
            l.setStatus(status);
            leaveRepository.save(l);
        });
    }
}
