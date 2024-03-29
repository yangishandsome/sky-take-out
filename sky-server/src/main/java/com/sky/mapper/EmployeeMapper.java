package com.sky.mapper;

import com.sky.annotation.AutoFillAnno;
import com.sky.dto.EditPasswordDTO;
import com.sky.dto.EmployeeDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
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

    @AutoFillAnno(value = OperationType.INSERT)
    void add(Employee employee);

    List<Employee> pageQuery(String name);

    @AutoFillAnno(value = OperationType.UPDATE)
    void updateStatus(Employee employee);

    @Select("select id, name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user" +
            " from sky_take_out.employee where id = #{id}")
    Employee queryById(Integer id);

    @AutoFillAnno(value = OperationType.UPDATE)
    void updateEmp(Employee employee);

    @AutoFillAnno(value = OperationType.UPDATE)
    @Update("update sky_take_out.employee set password = #{newPassword} , update_time = #{updateTime}" +
            " where id = #{empId} and password = #{oldPassword}")
    Integer updatePassword(EditPasswordDTO editPasswordDTO);
}
