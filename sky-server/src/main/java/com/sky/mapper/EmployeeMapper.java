package com.sky.mapper;

import com.sky.dto.EmployeeDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select id, name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user" +
            " from sky_take_out.employee where username = #{username}")
    Employee getByUsername(String username);

    void add(Employee employee);

    List<Employee> pageQuery(String name);

    @Update("update sky_take_out.employee set status = #{status} where id = #{id}")
    void setStatus(Integer status, Long id);

    @Select("select id, name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user" +
            " from sky_take_out.employee where id = #{id}")
    Employee queryById(Integer id);

    void updateEmp(Employee employee);

    @Update("update sky_take_out.employee set password = #{newPassword} , update_time = #{updateTime}" +
            " where id = #{id} and password = #{oldPassword}")
    Integer editPassword(Integer empId, String newPassword, String oldPassword, LocalDateTime updateTime);
}
