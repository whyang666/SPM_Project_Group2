package com.buptsse.spm.action;

import com.buptsse.spm.domain.Course;
import com.buptsse.spm.domain.Exam;
import com.buptsse.spm.domain.User;
import com.buptsse.spm.service.IExamService;
import com.buptsse.spm.service.IUserService;
import com.buptsse.spm.service.ISelectCourseService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author BUPT-TC
 * @date 2015年11月23日 下午4:17
 * @description 实现文件上传相关功能
 * @modify BUPT-TC
 * @modifyDate 
 */
public class UploadAction extends ActionSupport{

	private static Logger LOG = LoggerFactory.getLogger(UploadAction.class);
	// 上传文件存放路径
	private final static String UPLOADDIR = "/upload";
	// 上传文件集合
	private List<File> file;
	// 上传文件名集合
	private List<String> fileFileName;
	// 上传文件内容类型集合
	private List<String> fileContentType;

	@Resource
	private ISelectCourseService selectCourseService;

	@Resource
	private IExamService examService;
	@Resource
	private IUserService userService;
	/**
	 * 学生信息导入
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String insertUserInfo() throws FileNotFoundException, IOException {
		String msg = "";

		try {
			//判断文件类型
			String[][] scoreList = getData(file.get(0),1);

			int rowNum=scoreList.length;
			User newu;
			if(rowNum>0){
				for(int i=0;i<rowNum;++i){
					newu=new User();
					newu.setId(scoreList[i][0]);
					newu.setPassword(scoreList[i][2]);
					newu.setUserName(scoreList[i][1]);
					newu.setPosition(scoreList[i][3]);
					newu.setUserId(scoreList[i][4]);
					newu.setEmail(scoreList[i][5]);


					if("".equals(newu.getUserId())){
						msg = "学生信息上传失败，表格中学生id不能为空！";
						break;
					}else{
						//selectCourseService.saveOrUpdate(course);
						userService.addUser(newu);
						msg = "学生信息导入成功！";
					}
				}
			}else{
				msg = "无学生数据，请重新选择文件！";
			}


		} catch (Exception ex) {
			msg = "学生信息导入失败，请重新选择文件";
			System.out.println("学生信息导入失败失败!");
			ex.printStackTrace();
		}

		ServletActionContext.getResponse().getWriter().write(msg);

		return null;
	}



	/**
	 *
	 * @return
	 */
	public String uploadFileTest() throws IOException {
		String realpath = ServletActionContext.getServletContext().getRealPath("/whyangupload");
		//System.exit(0);
		LOG.error("userID:");
		LOG.error("realpath:！！！！！！！！！！！！？？？？？？？？？？？？？？ "+realpath);
		if (file != null) {
			File savefile = new File(new File("F:/logs/S2316S411H436/whyangupload"), fileFileName.get(0));
			if (!savefile.getParentFile().exists())
				savefile.getParentFile().mkdirs();
			FileUtils.copyFile(file.get(0), savefile);
			ActionContext.getContext().put("message", "文件上传成功");
		}

		return SUCCESS;
	}

	/**
	 * 上传文件
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String uploadFile() throws FileNotFoundException, IOException {
			String msg = "";
		try {
			InputStream in = new FileInputStream(file.get(0));
			String dir = ServletActionContext.getRequest().getRealPath(
					UPLOADDIR);
			LOG.info("readPath: " + dir);

			File fileLocation = new File(dir);
			// 此处也可以在应用根目录手动建立目标上传目录
			if (!fileLocation.exists()) {
				boolean isCreated = fileLocation.mkdir();
				if (!isCreated) {
					LOG.info("目录创建失败");
					// 目标上传目录创建失败,可做其他处理,例如抛出自定义异常等,一般应该不会出现这种情况。
					return "error";
				}

			}
			String fileName = this.getFileFileName().get(0);
			File uploadFile = new File(dir, fileName);
			OutputStream out = new FileOutputStream(uploadFile);
			byte[] buffer = new byte[1024 * 1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				LOG.info("read buffer");
				out.write(buffer, 0, length);
			}

			// 文件地点
			String fileWholeLocation = dir + "\\" + fileName;
			LOG.info(fileWholeLocation);

			File file = new File(fileWholeLocation);
			//File destFile  = new File("D:\\upload", fileName);
			//FileUtils.copyFile(uploadFile, destFile);
			String[][] result = getData(file, 1);

			in.close();
			out.close();
			msg = "文件上传成功！";		
			
		} catch (Exception ex) {
			msg = "文件上传失败，请联系管理员！";
			System.out.println("上传失败!");
			ex.printStackTrace();
		} 
		
		ServletActionContext.getResponse().getWriter().write(msg);
		
		return null;
	}


	/**
	 * 上传试卷文件，限定为xml文件，
	 * @return success
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String uploadExamFile() throws FileNotFoundException, IOException {
		String msg = "";//在页面展示的提示消息

		try {
			InputStream in = new FileInputStream(file.get(0));
			if(!this.getFileContentType().get(0).equals("text/xml")){ //判断上传文件的类型，对于非xml文件进行提示
				System.out.println("文件格式错误");
				msg="上传文件格式错误，请刷新后重试！";
				ServletActionContext.getResponse().getWriter().write(msg);
				return null;
			}
			String dir = ServletActionContext.getRequest().getRealPath("/examUpload");//获得当前上传路径
			LOG.error("readPath: " + dir);
			System.err.println(this.getFileContentType().get(0));
			File fileLocation = new File(dir);
			// 此处也可以在应用根目录手动建立目标上传目录
			if (!fileLocation.exists()) {
				boolean isCreated = fileLocation.mkdir();
				if (!isCreated) {
					LOG.info("目录创建失败");
					// 目标上传目录创建失败,可做其他处理,例如抛出自定义异常等,一般应该不会出现这种情况。
					return "error";
				}
			}
			String fileName = this.getFileFileName().get(0); //获取文件名
			File uploadFile = new File(dir, fileName); //创建文件
			OutputStream out = new FileOutputStream(uploadFile);
			byte[] buffer = new byte[1024 * 1024];
			int length;
			while ((length = in.read(buffer)) > 0) { //将上传的文件从临时文件写入
				LOG.info("read buffer");
				out.write(buffer, 0, length);
			}

			// 文件地点
			String fileWholeLocation = dir + "\\" + fileName;
			LOG.info(fileWholeLocation);

			File file = new File(fileWholeLocation);

			//文件解读
			parseExam(file);
			in.close();
			out.close();
			msg = "文件上传成功！";

		} catch (Exception ex) {
			msg = "文件上传失败，请联系管理员！";
			System.out.println("上传失败!");
			ex.printStackTrace();
		}

		ServletActionContext.getResponse().getWriter().write(msg);

		return null;
	}

	/**
	 * 使用dom4j解析上传的试卷，将试卷题目保存至数据库
	 * @param file
	 * @throws Exception
	 */
	public void parseExam(File file) throws Exception{
			SAXReader reader = new SAXReader();
			Document document = reader.read(file);
			Element root = document.getRootElement();
			Element testElement = root.element("test"); //获得根节点test
			String test = testElement.getText();
			System.out.println("ExamName: " + test);
			List questionNodes = root.elements("question"); //获得根节点下的每一个question元素

			for (Iterator it = questionNodes.iterator(); it.hasNext();) { //对每一个元素进行解析
				Exam exam = new Exam();
				Element question = (Element) it.next();
				exam.setExamName(test);
				exam.setQuestion(question.element("title").getText());
				exam.setNumber(examService.findExamMaxId(test)+1);
				exam.setAnswerRight(question.attributeValue("answer"));
				exam.setAnswerA(question.element("a").getText());
				exam.setAnswerB(question.element("b").getText());
				exam.setAnswerC(question.element("c").getText());
				exam.setAnswerD(question.element("d").getText());
				examService.insertExam(exam); //将问题保存至数据库
			}
	}

	
	/**
	 * 上传成绩单
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String uploadScoreFile() throws FileNotFoundException, IOException {
		String msg = "";
		
		try {
		    //判断文件类型
			String[][] scoreList = getData(file.get(0),1);
			
			int rowNum=scoreList.length;
			if(rowNum>0){
				for(int i=0;i<rowNum;++i){	
					Course course = new Course();
					course.setStudentId(scoreList[i][1]);
					course.setSyear(scoreList[i][2].substring(0,4));//对于年份需要特殊处理
					course.setName(scoreList[i][3]);
					//course.setClassId(scoreList[i][4]);
					int index = scoreList[i][4].lastIndexOf(".00");
					if(index>-1){
						//对于班级需要特殊处理	
						course.setClassId(scoreList[i][4].substring(0,index));
					}else{
						course.setClassId(scoreList[i][4]);
					}
					course.setDailyGrade(new BigDecimal(scoreList[i][5]));
					course.setMidGrade(new BigDecimal(scoreList[i][6]));
					course.setFinalGrade(new BigDecimal(scoreList[i][7]));
					course.setPracticeGrade(new BigDecimal(scoreList[i][8]));
					//导入成绩的所有的学生状态都为“选课成功”
					course.setStatus("2");
					//course.setEmail("");
					
					BigDecimal total=course.getDailyGrade().multiply(new BigDecimal(Double.valueOf(0.1)))
						.add(course.getMidGrade().multiply(new BigDecimal(Double.valueOf(0.1))))
						.add(course.getPracticeGrade().multiply(new BigDecimal(Double.valueOf(0.1))))
						.add(course.getFinalGrade().multiply(new BigDecimal(Double.valueOf(0.6))));
					
					course.setTotalGrade(total.setScale(2,BigDecimal.ROUND_HALF_UP));

					if("".equals(course.getStudentId())){
						msg = "成绩上传失败，表格中学生学号不能为空！";	
						break;
					}else{
						selectCourseService.saveOrUpdate(course);
						msg = "成绩上传成功！";	
					}
				}				
			}else{
				msg = "无成绩数据，请重新选择文件！";	
			}

			
		} catch (Exception ex) {
			msg = "成绩上传失败，请重新选择文件";	
			System.out.println("成绩上传失败!");
			ex.printStackTrace();
		} 
		
		ServletActionContext.getResponse().getWriter().write(msg);
		
		return null;
	}	
	
	
	
	
	/**
	 * 
	 * 读取Excel的内容，第一维数组存储的是一行中格列的值，二维数组存储的是多少个行
	 * 
	 * @param file
	 *            读取数据的源Excel
	 * 
	 * @param ignoreRows
	 *            读取数据忽略的行数，比喻行头不需要读入 忽略的行数为1
	 * 
	 * @return 读出的Excel中数据的内容
	 * 
	 * @throws FileNotFoundException
	 * 
	 * @throws IOException
	 * 
	 */

	public static String[][] getData(File file, int ignoreRows)

	throws FileNotFoundException, IOException {

		List<String[]> result = new ArrayList<String[]>();

		int rowSize = 0;

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

		// 打开HSSFWorkbook
		Workbook wb = null;

       try {
    	    wb = new XSSFWorkbook(new FileInputStream(file));
         } catch (Exception ex) {
        	 wb = new HSSFWorkbook(new FileInputStream(file));
         }
         
		//POIFSFileSystem fs = new POIFSFileSystem(in);

		//HSSFWorkbook wb = new HSSFWorkbook(fs);

		Cell cell = null;

		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {

			Sheet st = wb.getSheetAt(sheetIndex);

			// 第一行为标题，不取

			for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {

				Row row = st.getRow(rowIndex);

				if (row == null) {

					continue;

				}

				int tempRowSize = row.getLastCellNum() + 1;

				if (tempRowSize > rowSize) {

					rowSize = tempRowSize;

				}

				String[] values = new String[rowSize];

				Arrays.fill(values, "");

				boolean hasValue = false;

				for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {

					String value = "";

					cell = row.getCell(columnIndex);

					if (cell != null) {

						// 注意：一定要设成这个，否则可能会出现乱码

					//	cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						

						switch (cell.getCellType()) {

						case HSSFCell.CELL_TYPE_STRING:

							value = cell.getStringCellValue();

							break;

						case HSSFCell.CELL_TYPE_NUMERIC:

							if (HSSFDateUtil.isCellDateFormatted(cell)) {

								Date date = cell.getDateCellValue();

								if (date != null) {

									value = new SimpleDateFormat("yyyy-MM-dd")

									.format(date);

								} else {

									value = "";

								}

							} else {

								value = new DecimalFormat("0.00").format(cell.getNumericCellValue());

							}

							break;

						case HSSFCell.CELL_TYPE_FORMULA:

							// 导入时如果为公式生成的数据则无值

							if (!cell.getStringCellValue().equals("")) {

								value = cell.getStringCellValue();

							} else {

								value = cell.getNumericCellValue() + "";

							}

							break;

						case HSSFCell.CELL_TYPE_BLANK:

							break;

						case HSSFCell.CELL_TYPE_ERROR:

							value = "";

							break;

						case HSSFCell.CELL_TYPE_BOOLEAN:

							value = (cell.getBooleanCellValue() == true ? "Y"

							: "N");

							break;

						default:

							value = "";

						}

					}

					if (columnIndex == 0 && value.trim().equals("")) {

						break;

					}

					values[columnIndex] = rightTrim(value);

					hasValue = true;

				}

				if (hasValue) {

					result.add(values);

				}

			}

		}

		in.close();

		String[][] returnArray = new String[result.size()][rowSize];

		for (int i = 0; i < returnArray.length; i++) {

			returnArray[i] = (String[]) result.get(i);

		}

		return returnArray;

	}

	/**
	 * 
	 * 去掉字符串右边的空格
	 * 
	 * @param str
	 *            要处理的字符串
	 * 
	 * @return 处理后的字符串
	 * 
	 */

	public static String rightTrim(String str) {

		if (str == null) {

			return "";

		}
		return str.trim();

	}

	public ISelectCourseService getSelectCourseService() {
		return selectCourseService;
	}

	public void setSelectCourseService(ISelectCourseService selectCourseService) {
		this.selectCourseService = selectCourseService;
	}
	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}
	public List<File> getFile() {
		return file;
	}

	public void setFile(List<File> file) {
		this.file = file;
	}

	public List<String> getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(List<String> fileFileName) {
		this.fileFileName = fileFileName;
	}

	public List<String> getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(List<String> fileContentType) {
		this.fileContentType = fileContentType;
	}

	/*	public String execute() throws Exception {
		uploadFile(0);
		return "success";
	}
*/


	public IExamService getExamService() {
		return examService;
	}

	public void setExamService(IExamService examService) {
		this.examService = examService;
	}
}
