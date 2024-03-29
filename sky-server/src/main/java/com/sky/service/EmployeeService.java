package com.sky.service;

import com.sky.dto.EditPasswordDTO;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void add(EmployeeDTO employeeDTO);

    PageResult pageQuery(EmployeePageQueryDTO pageQueryDTO);

    void updateStatus(Integer status, Long id);

    Employee queryById(Integer id);

    void updateEmp(EmployeeDTO employeeDTO);

    Integer updatePassword(EditPasswordDTO editPasswordDTO);
}
