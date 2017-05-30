/**
 *
 */
package com.buptsse.spm.service.impl;

import java.security.spec.PSSParameterSpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.buptsse.spm.dao.IScheduleDao;
import com.buptsse.spm.dao.ISpChapterVideoDao;
import com.buptsse.spm.domain.Schedule;
import com.buptsse.spm.domain.SpChapterVideo;
import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Service;

import com.buptsse.spm.dao.IUserDao;
import com.buptsse.spm.domain.User;
import com.buptsse.spm.service.IUserService;

/**
 * @author BUPT-TC
 * @date 2015年11月24日 下午3:53:50
 * @description
 */


@Transactional
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private IUserDao iUserDao;

    @Resource
    private IScheduleDao iScheduleDao;

    @Resource
    private ISpChapterVideoDao iSpChapterVideoDao;

    /* (non-Javadoc)
     * @see com.buptsse.spm.service.IUserService#findUser(java.lang.String, java.lang.String)
     */
    @Override
    public User findUser(String userId, String password) {
        // TODO Auto-generated method stub
        User user = new User();
        user.setUserId(userId);
        user.setPassword(password);
        user = iUserDao.findUser(user);
        if (user == null || !user.getPassword().equals(password)) {
            return null;
        } else {
            return user;
        }
    }

    public User findUser(String userId) {
        User user = new User();
//		user.setUserName(userName);
        user.setId(userId);
        user = iUserDao.findUserById(userId);
        if (user == null) {
            return null;
        } else {
            return user;
        }
    }


    /* (non-Javadoc)
     * @see com.buptsse.spm.service.IUserService#insertUser(com.buptsse.spm.domain.User)
     */
    @Override
    public boolean insertUser(User user) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.buptsse.spm.service.IUserService#searchUser(java.lang.String)
     */
    @Override
    public List<User> searchUser(String choose) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.buptsse.spm.service.IUserService#deleteUser(java.lang.String)
     */
    @Override
    public boolean deleteUser(String id) {
        // TODO Auto-generated method stub
        User user = new User();
        user = iUserDao.findUserById(id);
        //获取当前用户
        User test = new User();
        HttpSession session = ServletActionContext.getRequest().getSession();
        test = (User) session.getAttribute("user");
        String sid = test.getId();

        if (sid.equals(id)) return false;//选中自己不能删除
        else {
            return iUserDao.deleteUser(user);
        }
    }

    /* (non-Javadoc)
     * @see com.buptsse.spm.service.IUserService#addUser(com.buptsse.spm.domain.User)
     */
    @Override
    public boolean addUser(User user) {
        return iUserDao.addUser(user);
    }

    public boolean addUserWithVideo(User user) {
        List<SpChapterVideo> videos = iSpChapterVideoDao.findAllInfo();
        for (SpChapterVideo video : videos) {
            Schedule schedule = new Schedule();
            schedule.setChapter_id(video.getChapter_id());
            schedule.setUserid(user.getId());
            schedule.setVideo_step_order(video.getVideo_step_order());
            schedule.setPercent(0);
            iScheduleDao.saveSchedule(schedule);
        }
        return true;
    }

    @Override
    public boolean updateUser(User user) {

        return iUserDao.updateUser(user);
    }

    @Override
    public List findPage(Map param, Integer page, Integer rows) {
        // TODO Auto-generated method stub
        String hql = "from User where 1=1 ";
        List paramList = new ArrayList();
        Iterator iter = param.keySet().iterator();

        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value = (String) param.get(key);
            if (!"".equals(value)) {
                hql += "and " + key + "=? ";
                paramList.add(value);
            }
        }

        return iUserDao.findPage(hql, paramList, page, rows);

    }

    @Override
    public Long count(Map param) {
        // TODO Auto-generated method stub
        String hql = "select count(*) from User where 1=1 ";
        List paramList = new ArrayList();
        Iterator iter = param.keySet().iterator();

        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value = (String) param.get(key);
            System.out.println("&&&&&value&&&&:" + value);
            if (!"".equals(value)) {
                hql += "and " + key + "=? ";
                paramList.add(value);
            }
        }

        return iUserDao.countUser(hql, paramList);
    }

    @Override
    public List<User> findStudents() {
        return iUserDao.findStudents();
    }


}
