package com.valleon.rewardyourteacherapi.service.impl;

import com.valleon.rewardyourteacherapi.domain.dao.AppUserDao;
import com.valleon.rewardyourteacherapi.domain.dao.StudentDao;
import com.valleon.rewardyourteacherapi.domain.dao.TeacherDao;
import com.valleon.rewardyourteacherapi.domain.dao.TransactionDao;
import com.valleon.rewardyourteacherapi.domain.entities.AppUser;
import com.valleon.rewardyourteacherapi.domain.entities.Student;
import com.valleon.rewardyourteacherapi.domain.entities.Teacher;
import com.valleon.rewardyourteacherapi.domain.entities.transact.Transaction;
import com.valleon.rewardyourteacherapi.infrastructure.exceptionHandlers.CustomNotFoundException;
import com.valleon.rewardyourteacherapi.service.payload.TransactionService;
import com.valleon.rewardyourteacherapi.service.payload.response.TransactionResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionDao transactionDao;

    private final StudentDao studentDao;

    private TeacherDao teacherDao;

    private final AppUserDao userDao;

    @Override
    public List<TransactionResponse> getStudentTransactions(int offset, int pageSize) {
       Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

       if(!(principal instanceof UserDetails)){
           throw new CustomNotFoundException("User not found");
       }

       String email = ((UserDetails)principal).getUsername();
       AppUser appUser = userDao.findAppUserByEmail(email)
               .orElseThrow(() -> new CustomNotFoundException("User not found."));

       Student student = studentDao.getStudentByAppUser(appUser);

        Pageable pageable= PageRequest.of(offset, pageSize);

        Page<Transaction> pageList = transactionDao.findTransactionByStudent(pageable, student);

        List<TransactionResponse> transactionResponses = new ArrayList<>();

        pageList.forEach(page ->{
            TransactionResponse transactionResponse = TransactionResponse.builder()
                    .transactionType(page.getTransactionType())
                    .amount(page.getAmount())
                    .description(page.getDescription())
                    .createdAt(page.getCreatedAt())
                    .build();

            transactionResponses.add(transactionResponse);
        });

        return transactionResponses;
    }

    @Override
    public List<TransactionResponse> getTeacherTransactions(int offset, int pageSize) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!(principal instanceof UserDetails)){
            throw new CustomNotFoundException("user not found");
        }

        String email = ((UserDetails)principal).getUsername();
        AppUser appUser = userDao.findAppUserByEmail(email)
                .orElseThrow(()-> new CustomNotFoundException("User not found"));

        Teacher teacher = teacherDao.getTeacherByAppUser(appUser);

        Pageable pageable = PageRequest.of(offset, pageSize);

        Page<Transaction> pageList = transactionDao.findTransactionByTeacher(pageable, teacher);

        List<TransactionResponse> transactionResponses = new ArrayList<>();

        pageList.forEach(page ->{
            TransactionResponse transactionResponse = TransactionResponse.builder()
                    .transactionType(page.getTransactionType())
                    .amount(page.getAmount())
                    .description(page.getDescription())
                    .createdAt(page.getCreatedAt())
                    .build();

            transactionResponses.add(transactionResponse);
        });

        return transactionResponses;
    }
























}
