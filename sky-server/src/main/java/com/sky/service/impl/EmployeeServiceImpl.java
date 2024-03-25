package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    HttpServletRequest request;
    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void add(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setId(employeeDTO.getId());
        employee.setName(employeeDTO.getName());
        employee.setSex(employeeDTO.getSex());
        employee.setPhone(employeeDTO.getPhone());
        employee.setUsername(employeeDTO.getUsername());
        employee.setIdNumber(employeeDTO.getIdNumber());

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());


        String token = request.getHeader("token");
        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
        Integer Id = (Integer) claims.get(JwtClaimsConstant.EMP_ID);


        employee.setCreateUser(Id.longValue());
        employee.setUpdateUser(Id.longValue());
        employee.setStatus(1);

        employeeMapper.add(employee);

    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO pageQueryDTO) {
        Integer start = pageQueryDTO.getPage();
        Integer pageSize = pageQueryDTO.getPageSize();
        String name = pageQueryDTO.getName();
        PageHelper.startPage(start, pageSize);

        Page<Employee> page = (Page<Employee>) employeeMapper.pageQuery(name);

        Long total = page.getTotal();
        List<Employee> result = page.getResult();

        PageResult pageResult = new PageResult(total, result);

        return pageResult;
    }

    @Override
    public void setStatus(Integer status, Long id) {
        employeeMapper.setStatus(status, id);
    }

    @Override
    public Employee queryById(Integer id) {
        return employeeMapper.queryById(id);
    }

    @Override
    public void updateEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setId(employeeDTO.getId());
        employee.setName(employeeDTO.getName());
        employee.setSex(employeeDTO.getSex());
        employee.setPhone(employeeDTO.getPhone());
        employee.setUsername(employeeDTO.getUsername());
        employee.setIdNumber(employeeDTO.getIdNumber());

        String token = request.getHeader("token");
        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
        Integer Id = (Integer) claims.get(JwtClaimsConstant.EMP_ID);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(Id.longValue());

        employeeMapper.updateEmp(employee);
    }

    @Override
    public Integer editPassword(String newPassword, String oldPassword) {
        String token = request.getHeader("token");
        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
        Integer empId = (Integer) claims.get(JwtClaimsConstant.EMP_ID);

        oldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        newPassword = DigestUtils.md5DigestAsHex((newPassword.getBytes()));

        LocalDateTime updateTime = LocalDateTime.now();
        Integer flag = employeeMapper.editPassword(empId, newPassword, oldPassword, updateTime);
        return flag;
    }

}
