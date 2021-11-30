package com.founder.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewMybatisGenerator {

	public static void main(String[] args) throws Exception {

		RewMybatisGenerator mg = new RewMybatisGenerator("com.founder.rew", "REW_",null);
		mg.setAuthor("yuyongjun");
		mg.setWorkspacePath("G:\\IDEA\\idea_project_jsjc\\rew-parent");
		mg.setModelPath("rew-facade\\src\\main\\java\\com\\founder\\rew\\model\\");
		mg.setServicePath("rew-facade\\src\\main\\java\\com\\founder\\rew\\service\\");
		mg.setServiceImplPath("rew-service\\src\\main\\java\\com\\founder\\rew\\service\\impl\\");
		mg.setSqlMapperPath("rew-service\\src\\main\\resources\\mybatis\\");
		mg.setControllerPath("rew-web\\src\\main\\java\\com\\founder\\rew\\web\\controller\\");

//		MybatisGenerator3 mg = new MybatisGenerator3("com.founder.ssm.foundation", "","foundation");
//		mg.setAuthor("yuyongjun");
//		mg.setWorkspacePath("G:\\idea_project\\foundation-parent");
//		mg.setModelPath("foundation-facade\\src\\main\\java\\com\\founder\\ssm\\foundation\\model\\");
//		mg.setServicePath("foundation-facade\\src\\main\\java\\com\\founder\\ssm\\foundation\\service\\");
//		mg.setServiceImplPath("foundation-service\\src\\main\\java\\com\\founder\\ssm\\foundation\\service\\impl\\");
//		mg.setSqlMapperPath("foundation-service\\src\\main\\resources\\mybatis\\");
//		mg.setControllerPath("foundation-facade\\src\\main\\java\\com\\founder\\ssm\\foundation\\service\\");

/*		MybatisGenerator2 mg = new MybatisGenerator2("com.jsjc.ei", "BD_T_",null);
		mg.setAuthor("yuyongjun");
		mg.setWorkspacePath("G:\\idea_project_jsjc\\ei-parent");
		mg.setModelPath("ei-facade\\src\\main\\java\\com\\jsjc\\ei\\model\\");
		mg.setServicePath("ei-facade\\src\\main\\java\\com\\jsjc\\ei\\service\\");
		mg.setServiceImplPath("ei-service\\src\\main\\java\\com\\jsjc\\ei\\service\\impl\\");
		mg.setSqlMapperPath("ei-service\\src\\main\\resources\\mybatis\\");
		mg.setControllerPath("ei-web\\src\\main\\java\\com\\jsjc\\ei\\controller\\");*/

		//String[] tables = new String[]{"REW_RISK_MODEL","REW_RISK_NOTICE","REW_RISK_WARNING_LOG","REW_RISK_WARNING_LOG_DETAIL","REW_RULE","REW_RULE_EXPRESSION","REW_RULE_EXPRESSION_CONDITION","REW_WARNING_OBJECT","REW_WARNING_OBJECT_ATTRIBUTE","REW_WARNING_TEMPLATE"};
		String[] tables = new String[]{"SYS_USER"};


		for(int i=0;i<tables.length;i++){

			//mg.generatorService(tables[i], true);
			//mg.generatorServiceImpl(tables[i], true);
			//mg.generatorSpringMVCController(tables[i], true);
			mg.generatorModel(tables[i], true);
			mg.generatorSqlMap(tables[i], true);

			//mg.generatorJSP_LIST(tables[i], true);
			//mg.generatorJSP_ADD(tables[i], true);
			//mg.generatorJSP_UPDATE(tables[i], true);
			//mg.generatorJSP_VIEW(tables[i], true);BANK_ACCOUNT

		}
	}

	private String packagePrefix;// 包名前缀

	private String workspacePath;//工作目录绝对路径

	private String controllerPath;//Controller源代码绝对路径

	private String servicePath;//Service源代码绝对路径

	private String serviceImplPath;//ServiceImpl源代码绝对路径

	private String modelPath;//Model源代码绝对路径

	private String SqlMapperPath;//sqlMapper.xml源代码绝对路径

	private String tablePrefix;//表名前缀

	private String dbName;//表名前缀,mysql数据库名(区分大小写)，oracle数据库该值为空

	private String author;// 文件创建者

	private final static String CONTROLLER = "controller";
	private final static String ACTION = "controller";
	private final static String MODEL = "model";
	private final static String SERVICE = "service";
	private final static String SERVICEIMPL = SERVICE + "." + "impl";

	public RewMybatisGenerator(String packagePrefix, String tablePrefix, String dbName) {
		this.packagePrefix = packagePrefix;
		this.tablePrefix = tablePrefix;
		this.dbName = dbName;
	}

	public void generatorModel(String tableName, boolean writeable) throws Exception {
		StringBuffer text = new StringBuffer("");

		String packageStr = packagePrefix + "." + MODEL;
		String modelName = this.getClassName(tableName) + "Model";

		text.append("package " + packageStr + ";\n\n");
		text.append("import java.util.Date;\n\n");
		text.append("import lombok.Data;\n\n");
		text.append("import org.springframework.format.annotation.DateTimeFormat;\n");
		text.append("import com.alibaba.fastjson.annotation.JSONField;\n");

		text.append("import com.founder.ssm.core.model.BaseModel;\n\n");
		text = generateNotes(text, MODEL);
		text.append("@Data \n");
		text.append("public class " + modelName + " extends BaseModel {\n\n");

		text.append("	private static final long serialVersionUID = 1L;\n\n");
		FieldDesc[] fieldDescs = this.getAllField(tableName);

		//将数据库字段属性转成java属性，并生成Model属性
		boolean isHavaDate=false;
		for (int i = 0; i < fieldDescs.length; i++) {
			FieldJavaDesc fieldJavaDesc = getFieldJavaDesc(fieldDescs[i]);
			//不生成createBy,createDt,updateBy,updateDt
			if("createBy,createDt,updateBy,updateDt".indexOf(fieldJavaDesc.getFieldName())>-1){
				continue;
			}

			if (fieldJavaDesc.getFieldType().equals("XClob")) {
				fieldJavaDesc.setFieldType("String");
			}

			text.append("	/** "+fieldJavaDesc.getFieldComment()+" */\n");
			if (fieldJavaDesc.getFieldType().equals("Date")) {
				isHavaDate = true;
				text.append("	@DateTimeFormat(pattern=\"yyyy-MM-dd\")\n");
				text.append("	@JSONField(format=\"yyyy-MM-dd\")\n");
			}

			text.append("	private " + fieldJavaDesc.getFieldType() +" "+ fieldJavaDesc.getFieldName() + ";\n\n");
		}
		if(!isHavaDate){
			text = new StringBuffer(text.toString().replace("import java.util.Date;\n\n", ""));
			text = new StringBuffer(text.toString().replace("import org.springframework.format.annotation.DateTimeFormat;\n", ""));
			text = new StringBuffer(text.toString().replace("import com.alibaba.fastjson.annotation.JSONField;\n", ""));
		}

		//将数据库字段属性转成java属性，并生成Model属性的get，set方法
		/*for (int i = 0; i < fieldDescs.length; i++) {
			FieldJavaDesc fieldJavaDesc = getFieldJavaDesc(fieldDescs[i]);

			//不生成createBy,createDt,updateBy,updateDt
			if("createBy,createDt,updateBy,updateDt".indexOf(fieldJavaDesc.getFieldName())>-1){
				continue;
			}

			if (fieldJavaDesc.getFieldType().equals("XClob")) {
				fieldJavaDesc.setFieldType("String");
			}
			text.append("	public " + fieldJavaDesc.getFieldType() + " get");
			text.append(setFirstUpperCase(fieldJavaDesc.getFieldName()));
			text.append("() {\n");
			text.append("		return " + fieldJavaDesc.getFieldName() + ";\n");
			text.append("	}\n\n");

			text.append("	public void set");
			text.append(setFirstUpperCase(fieldJavaDesc.getFieldName()) + "(");
			text.append(fieldJavaDesc.getFieldType() + " ");
			text.append(fieldJavaDesc.getFieldName() + ") {\n");
			text.append("		this." + fieldJavaDesc.getFieldName() + " = ");
			text.append(fieldJavaDesc.getFieldName() + ";\n");
			text.append("	}\n\n");
		}*/
		text.append("}");

		//文件绝对路径
		String filePath = workspacePath + File.separator + modelPath;
		String fileName = modelName + ".java";

		createFile(filePath, fileName, writeable, text.toString());

	}

	public void generatorService(String tableName, boolean writeable) throws Exception {
		StringBuffer text = new StringBuffer("");

		String packageStr = packagePrefix + "." + SERVICE;
		String className = getClassName(tableName);
		String modelName = className + "Model";
		String serviceName = className +"Service";
		//String lowerServiceName = setFirstLowerCase(serviceName);

		text.append("package " + packageStr + ";\n\n");
		text.append("import com.founder.ssm.core.service.BaseService;\n\n");
		text.append("import "+packagePrefix+"."+MODEL+"."+modelName+";\n\n");

		text = generateNotes(text, SERVICE);
		text.append("public interface " + serviceName + " extends BaseService<"+modelName+">" + "  {\n\n");
		text.append("}");

		//String filePath = srcPath + File.separator + packageStr.replace(".", File.separator) + File.separator;
		String filePath = workspacePath + File.separator + servicePath;
		String fileName = serviceName + ".java";

		createFile(filePath, fileName, writeable, text.toString());
	}

	public void generatorServiceImpl(String tableName, boolean writeable) throws Exception {
		StringBuffer text = new StringBuffer("");

		String packageStr = packagePrefix + "." + SERVICEIMPL;
		String className = getClassName(tableName);
		String modelName = className + "Model";
		String serviceName = className +"Service";
		String lowerServiceName = setFirstLowerCase(serviceName);
		String serviceImplName = className +"ServiceImpl";

		text.append("package " + packageStr + ";\n\n");
		text.append("import org.springframework.stereotype.Component;\n\n");

		text.append("import com.alibaba.dubbo.config.annotation.Service;\n");
		text.append("import com.founder.ssm.core.service.impl.BaseServiceImpl;\n");
		text.append("import "+packagePrefix+"."+MODEL+"."+modelName+";\n");
		text.append("import "+packagePrefix+"."+SERVICE+"."+serviceName+";\n\n");

		text = generateNotes(text, SERVICEIMPL);
		text.append("@Component(\""+lowerServiceName+"\")\n");
		text.append("@Service(interfaceClass = "+serviceName+".class)\n");
		text.append("public class "+serviceImplName+" extends BaseServiceImpl<"+modelName+"> implements "+serviceName+" {\n\n");
		text.append("	public "+serviceImplName+"(){\n");
		text.append("		this.setNamespace(\""+className+"\");\n");
		text.append("	}\n\n");
		text.append("}");

		//String filePath = srcPath + File.separator + packageStr.replace(".", File.separator) + File.separator;
		String filePath = workspacePath + File.separator + serviceImplPath;
		String fileName = serviceImplName + ".java";

		createFile(filePath, fileName, writeable, text.toString());
	}

	public void generatorSpringMVCController(String tableName, boolean writeable) throws Exception {
		StringBuffer text = new StringBuffer("");

		String packageStr = packagePrefix + "." + CONTROLLER;
		String className = getClassName(tableName);
		//String lowerClassName = setFirstLowerCase(className);
		String modelName = className + "Model";
		//String lowerModelName = setFirstLowerCase(modelName);
		String serviceName = className +"Service";
		String lowerServiceName = setFirstLowerCase(serviceName);
		//String serviceImplName = className +"ServiceImpl";
		String controllerName = className +"Controller";
		//String lowerActionName = setFirstLowerCase(actionName);
		text.append("package " + packageStr + ";\n\n");

		text.append("import java.util.ArrayList;\n");
		text.append("import java.util.List;\n\n");

		text.append("import javax.annotation.Resource;\n");
		text.append("import javax.servlet.http.HttpServletRequest;\n");
		text.append("import javax.servlet.http.HttpServletResponse;\n\n");

		text.append("import org.apache.commons.lang3.StringUtils;\n");
		text.append("import org.slf4j.Logger;\n");
		text.append("import org.slf4j.LoggerFactory;\n");

		text.append("import org.springframework.beans.BeanUtils;\n");
		text.append("import org.springframework.context.annotation.Scope;\n");
		text.append("import org.springframework.web.bind.annotation.*;\n");
		text.append("import org.springframework.web.servlet.ModelAndView;\n\n");

		text.append("import com.alibaba.dubbo.config.annotation.Reference;\n");
		text.append("import com.alibaba.fastjson.JSONObject;\n");
		text.append("import com.alibaba.fastjson.serializer.SerializerFeature;\n\n");

		text.append("import com.founder.ssm.core.vo.BaseVO;\n");
		text.append("import com.founder.ssm.core.common.SearchCondition;\n");
		text.append("import com.founder.ssm.core.action.BaseAction;\n");
		text.append("import com.founder.ssm.core.exception.SystemException;\n");
		text.append("import com.founder.ssm.web.common.CommonStatus;\n\n");

		text.append("import io.swagger.annotations.Api;\n");
		text.append("import io.swagger.annotations.ApiImplicitParam;\n");
		text.append("import io.swagger.annotations.ApiImplicitParams;\n");
		text.append("import io.swagger.annotations.ApiOperation;\n");
		text.append("import io.swagger.annotations.ApiResponse;\n");
		text.append("import io.swagger.annotations.ApiResponses;\n");
		text.append("import springfox.documentation.annotations.ApiIgnore;\n\n");

		text.append("import com.github.pagehelper.PageInfo;\n");
		text.append("import "+packagePrefix+"."+MODEL+"."+modelName+";\n");
		text.append("import "+packagePrefix+"."+SERVICE+"."+serviceName+";\n");

		text = generateNotes(text, CONTROLLER);
		text.append("@RestController\n");
		text.append("@RequestMapping(\"/"+className+"\")");
		text.append("@Scope(\"prototype\")\n");
		//text.append("@RequestMapping(\""+lowerActionName+"\")\n");
		text.append("@Api(tags={\"XXX接口\"})\n");
		text.append("public class "+controllerName+" extends BaseAction{\n\n");

		text.append("	protected final Logger logger = LoggerFactory.getLogger(this.getClass());\n\n");

		//text.append("	@Resource(name =\""+lowerServiceName+"\")\n");
		text.append("	@Reference\n");
		text.append("	private "+serviceName+" "+lowerServiceName+";\n\n");

		//text.append("	//跳转到列表\n");
		//text.append("	@RequestMapping(value = \"toList\")\n");
		//text.append("	public ModelAndView toList(String id, HttpServletRequest request, HttpServletResponse response){\n");
		//text.append("		ModelAndView mav = new ModelAndView(\""+lowerClassName+"/"+lowerClassName+"List\");\n");
		//text.append("		return mav;\n");
		//text.append("	}\n\n");

		//text.append("	//查询多条\n");
		//text.append("	@RequestMapping(value = \"selectListPage\", method = RequestMethod.GET, produces = \"application/json;charset=UTF-8\")\n");
		text.append("	@ApiOperation(value=\"XXX列表\")\n");
		text.append("	@ApiImplicitParams({\n");
		text.append("		@ApiImplicitParam(name = \"length\", value = \"每页大小\", required = true, dataType = \"int\"),\n");
		text.append("		@ApiImplicitParam(name = \"currPage\", value = \"当前页码\", required = true, dataType = \"int\")\n");
		text.append("	})\n");
		text.append("	@ApiResponses({\n");
		text.append("		@ApiResponse(code = CommonStatus.OK, message = \"操作成功\"),\n");
		text.append("        @ApiResponse(code = CommonStatus.EXCEPTION, message = \"服务器内部异常\"),\n");
		text.append("    })\n");
		text.append("	@GetMapping(value = \"/list\", produces = \"application/json;charset=UTF-8\")\n");
		text.append("	public String list(@RequestParam(\"length\") int length, @RequestParam(\"currPage\") int currPage){\n");
		text.append("		try {\n\n");
		text.append("			searchCondition.setPageSize(length);\n");
		text.append("			searchCondition.setCurrPage(currPage);\n");
		text.append("			PageInfo<"+modelName+"> rows = "+lowerServiceName+".selectListPage(searchCondition);\n");
		text.append("			List<BaseVO> list = new ArrayList<BaseVO>();\n");
		text.append("			for("+modelName+" model : rows.getList()){\n");
		text.append("				BaseVO vo = new BaseVO();\n");
		text.append("				BeanUtils.copyProperties(model, vo);\n");
		text.append("				list.add(vo);\n");
		text.append("			}\n\n");

		text.append("			dataTablesResponse.setData(list, rows);\n\n");

		text.append("		} catch (SystemException e) {\n");
		text.append("			logger.error(e.getMessage());\n");
		text.append("			dataTablesResponse.error(e.getMessage());\n");
		text.append("		} catch (Exception e) {\n");
		text.append("			dataTablesResponse.error();\n");
		text.append("			logger.error(e.getMessage(), e);\n");
		text.append("		}\n");
		text.append("		return JSONObject.toJSONString(dataTablesResponse, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteMapNullValue);\n");
		text.append("	}\n\n");

		//text.append("	//编辑跳转\n");
		//text.append("	@RequestMapping(value = \"toSave\")\n");
		//text.append("	public ModelAndView toSave(String dealMark, String id, HttpServletRequest request, HttpServletResponse response){\n");
		//text.append("		ModelAndView mav = new ModelAndView(\""+lowerClassName+"/"+lowerClassName+"Edit\");\n");
		//text.append("		try {\n");
		//text.append("			"+modelName+" model = new "+modelName+"();\n");
		//text.append("			if(dealMark.equals(\"update\")) {\n");
		//text.append("				model = "+lowerServiceName+".selectById(id);\n");
		//text.append("			}\n");
		//text.append("			mav.addObject(\"model\", model);\n");
		//text.append("		} catch (SystemException e) {\n");
		//text.append("			logger.error(e.getMessage(), e);\n");
		//text.append("		} catch (Exception e) {\n");
		//text.append("			logger.error(e.getMessage(), e);\n");
		//text.append("		}\n");
		//text.append("		return mav;\n");
		//text.append("	}\n\n");

		//text.append("	//保存\n");
		//text.append("	@RequestMapping(value = \"save\", method = RequestMethod.POST, produces = \"application/json;charset=UTF-8\")\n");
		//text.append("	public @ResponseBody String save("+modelName+" model, String dealMark, HttpServletRequest request, HttpServletResponse response){\n");
		//text.append("		try {\n\n");
		//text.append("			if(dealMark.equals(\"add\")) {\n");
		//text.append("				"+lowerServiceName+".insert(model);\n");
		//text.append("			}else if(dealMark.equals(\"update\")) {\n");
		//text.append("				"+lowerServiceName+".update(model);\n");
		//text.append("			}\n");
		//text.append("		} catch (SystemException e) {\n");
		//text.append("			baseResponse.error();\n");
//		text.append("			logger.error(e.getMessage(), e);\n");
//		text.append("		} catch (Exception e) {\n");
//		text.append("			baseResponse.error();\n");
//		text.append("			logger.error(e.getMessage(), e);\n");
//		text.append("		}\n");
//		text.append("		return JSONObject.toJSONString(baseResponse);\n");
//		text.append("	}\n\n");
//
//		text.append("	//删除\n");
//		text.append("	@RequestMapping(value = \"delete\", produces = \"application/json;charset=UTF-8\")\n");
//		text.append("	public @ResponseBody String delete(String pkIds, HttpServletRequest request, HttpServletResponse response){\n");
//		text.append("		try {\n\n");
//		text.append("			"+lowerServiceName+".deleteBatch(pkIds.split(\",\"));\n");
//		text.append("		} catch (SystemException e) {\n");
//		text.append("			baseResponse.error();\n");
//		text.append("			logger.error(e.getMessage(), e);\n");
//		text.append("		} catch (Exception e) {\n");
//		text.append("			baseResponse.error();\n");
//		text.append("			logger.error(e.getMessage(), e);\n");
//		text.append("		}\n");
//		text.append("		return JSONObject.toJSONString(baseResponse);\n");
//		text.append("	}\n\n");

		//text.append("	//详情跳转\n");
		//text.append("	@RequestMapping(value = \"view\")\n");
		//text.append("	public ModelAndView view(String id, HttpServletRequest request, HttpServletResponse response){\n");
		//text.append("		ModelAndView mav = new ModelAndView(\""+lowerClassName+"/"+lowerClassName+"View\");\n");
		//text.append("		try {\n");
		//text.append("			"+modelName+" model = "+lowerServiceName+".selectById(id);\n");
		//text.append("			mav.addObject(\"model\", model);\n");

		//text.append("		} catch (SystemException e) {\n");
		//text.append("			logger.error(e.getMessage(), e);\n");
		//text.append("		} catch (Exception e) {\n");
		//text.append("			logger.error(e.getMessage(), e);\n");
		//text.append("		}\n");
		//text.append("		return mav;\n");
		//text.append("	}\n\n");

		text.append("	@ApiOperation(value=\"获取XXX详细信息\")\n");
		text.append("	@ApiImplicitParam(name = \"id\", value = \"XXXID\", required = true, dataType = \"String\", paramType = \"path\")\n");
		text.append("	@ApiResponses({ @ApiResponse(code = CommonStatus.OK, message = \"操作成功\"),\n");
		text.append("        @ApiResponse(code = CommonStatus.EXCEPTION, message = \"服务器内部异常\"),\n");
		text.append("   })\n");
		text.append("	@GetMapping(value = \"/detail/{id}\")\n");
		text.append("	public BaseResponse detail(@PathVariable(value = \"id\") String id){\n");
		text.append("		try {\n");
		text.append("			"+modelName+" model = "+lowerServiceName+".selectById(id);\n");
		text.append("			BaseVO vo = new BaseVO();\n");
		text.append("			BeanUtils.copyProperties(vo, model);\n");
		text.append("			dataResponse.setData(vo);\n");
		text.append("		} catch (SystemException e) {\n");
		text.append("			logger.error(e.getMessage());\n");
		text.append("			dataResponse.error(e.getMessage());\n");
		text.append("		} catch (Exception e) {\n");
		text.append("			dataResponse.error();\n");
		text.append("			logger.error(e.getMessage(), e);\n");
		text.append("		}\n");
		text.append("		return dataResponse;\n");
		text.append("	}\n\n");

		text.append("}\n\n");

		//String filePath = srcPath + File.separator + packageStr.replace(".", File.separator) + File.separator;
		String filePath = workspacePath + File.separator + controllerPath;
		String fileName = controllerName + ".java";

		createFile(filePath, fileName, writeable, text.toString());
	}

	public void generatorStruts2Action(String tableName, boolean writeable) throws Exception {
		StringBuffer text = new StringBuffer("");

		String packageStr = packagePrefix + "." + ACTION;
		String className = getClassName(tableName);
		String moduleName = setFirstLowerCase(className);
		String modelName = className + "Model";
		String lowerModelName = setFirstLowerCase(modelName);
		String serviceName = className +"Service";
		String lowerServiceName = setFirstLowerCase(serviceName);
		String serviceImplName = className +"ServiceImpl";
		String actionName = className +"Action";
		String lowerActionName = setFirstLowerCase(actionName);

		text.append("package " + packageStr + ";\n\n");

		text.append("import java.util.List;\n\n");

		text.append("import javax.annotation.Resource;\n\n");

		text.append("import org.slf4j.Logger;\n");
		text.append("import org.slf4j.LoggerFactory;\n");
		text.append("import org.apache.struts2.convention.annotation.Action;\n");
		text.append("import org.apache.struts2.convention.annotation.Namespace;\n");
		text.append("import org.apache.struts2.convention.annotation.ParentPackage;\n");
		text.append("import org.apache.struts2.convention.annotation.Result;\n");
		text.append("import org.apache.struts2.convention.annotation.Results;\n");
		text.append("import org.springframework.context.annotation.Scope;\n\n");

		text.append("import "+packagePrefix+".base.action.BaseAction;\n");
		text.append("import "+packagePrefix+".base.exception.SystemException;\n");
		text.append("import "+packagePrefix+".base.bean.BaseBeanRequest;\n");
		text.append("import "+packagePrefix+".common.MessageResources;\n");
		text.append("import "+packagePrefix+"."+MODEL+"."+modelName+";\n");
		text.append("import "+packagePrefix+"."+SERVICE+"."+serviceName+";\n\n");
		//text.append("import "+packagePrefix+".common.MessageResources;\n\n");

		text.append("@Namespace(\"/\")\n");
		text.append("@ParentPackage(\"default\")\n");
		text.append("@Action(\""+lowerActionName+"\")\n");
		text.append("@Results({\n");
		text.append("	  @Result(name=\"success\", type=\"json\",params={\"root\",\"baseResponse\"}),\n");
		text.append("	  @Result(name=\"toList\",location=\"/jsp/"+moduleName+"/"+moduleName+"List.jsp\"),\n");
		text.append("	  @Result(name=\"toAdd\",location=\"/jsp/"+moduleName+"/"+moduleName+"Add.jsp\"),\n");
		text.append("	  @Result(name=\"toUpdate\",location=\"/jsp/"+moduleName+"/"+moduleName+"Update.jsp\"),\n");
		text.append("	  @Result(name=\"view\",location=\"/jsp/"+moduleName+"/"+moduleName+"View.jsp\"),\n");
		text.append("})\n");
		text.append("@Scope(\"prototype\")\n");
		text.append("public class "+actionName+" extends BaseAction{\n\n");

		text.append("	protected final Logger logger = LoggerFactory.getLogger(this.getClass());\n\n");

		text.append("	private "+modelName+" "+lowerModelName+";\n\n");

		text.append("	private BaseBeanRequest searchBean = new BaseBeanRequest();\n\n");

		text.append("	@Resource(name =\""+setFirstLowerCase(serviceImplName)+"\")\n");
		text.append("	private "+serviceName+" "+lowerServiceName+";\n\n");

		text.append("	public String toList() throws Exception {\n");
		text.append("		return \"toList\";\n");
		text.append("	}\n\n");

		text.append("	public String list() throws Exception {\n");
		text.append("		try {\n");
		text.append("			searchCondition = this.getSearchCondition(searchBean);\n\n");
		text.append("			List<"+modelName+"> list = "+lowerServiceName+".selectList(searchCondition);\n");
		text.append("			Integer totalCount = "+lowerServiceName+".selectCount(searchCondition);\n");
		text.append("			dataGridBean.setTotal(totalCount);\n");
		text.append("			dataGridBean.setRows(list);\n");
		text.append("		} catch (SystemException e) {\n");
		text.append("			logger.error(\"SystemException:\", e);\n");
		text.append("			baseResponse.setStatue(MessageResources.getInstance().getString(\"E001\"));\n");
		text.append("			baseResponse.setMsg(e.getMessage());\n");
		text.append("		} catch (Exception e) {\n");
		text.append("			logger.error(\"\", e);\n");
		text.append("			baseResponse.setStatue(MessageResources.getInstance().getString(\"E001\"));\n");
		text.append("			baseResponse.setMsg(MessageResources.getInstance().getString(\"M001\"));\n");
		text.append("		}\n");
		text.append("		return \"dataGrid\";\n");
		text.append("	}\n\n");

		text.append("	public String toAdd() throws Exception {\n");
		text.append("		return \"toAdd\";\n");
		text.append("	}\n\n");

		text.append("	public String add() throws Exception {\n");
		text.append("		try {\n");
		text.append("			"+lowerServiceName+".insert("+lowerModelName+");\n");
		text.append("		} catch (SystemException e) {\n");
		text.append("			baseResponse.setStatue(MessageResources.getInstance().getString(\"E001\"));\n");
		text.append("			baseResponse.setMsg(e.getMessage());\n");
		text.append("		} catch (Exception e) {\n");
		text.append("			logger.error(e);\n");
		text.append("			baseResponse.setStatue(MessageResources.getInstance().getString(\"E001\"));\n");
		text.append("			baseResponse.setMsg(MessageResources.getInstance().getString(\"M001\"));\n");
		text.append("		}\n");
		text.append("		return \"success\";\n");
		text.append("	}\n\n");

		text.append("	public String toUpdate() throws Exception {\n");
		text.append("		String pkId = request.getParameter(\"pkId\");\n");
		text.append("		"+lowerModelName+" = "+lowerServiceName+".selectById(pkId);\n");
		text.append("		return \"toUpdate\";\n");
		text.append("	}\n\n");

		text.append("	public String update() throws Exception {\n");
		text.append("		try {\n");
		text.append("			"+lowerServiceName+".update("+lowerModelName+");\n");
		text.append("		} catch (SystemException e) {\n");
		text.append("			baseResponse.setStatue(MessageResources.getInstance().getString(\"E001\"));\n");
		text.append("			baseResponse.setMsg(e.getMessage());\n");
		text.append("		} catch (Exception e) {\n");
		text.append("			logger.error(e);\n");
		text.append("			baseResponse.setStatue(MessageResources.getInstance().getString(\"E001\"));\n");
		text.append("			baseResponse.setMsg(MessageResources.getInstance().getString(\"M001\"));\n");
		text.append("		}\n");
		text.append("		return \"success\";\n");
		text.append("	}\n\n");

		text.append("	public String delete() throws Exception {\n");
		text.append("		try {\n");
		text.append("			String pkIds = request.getParameter(\"pkIds\");\n");
		text.append("			"+lowerServiceName+".delete(\"deleteBatch\", pkIds.split(\",\"));\n");
		text.append("		} catch (SystemException e) {\n");
		text.append("			baseResponse.setStatue(MessageResources.getInstance().getString(\"E001\"));\n");
		text.append("			baseResponse.setMsg(e.getMessage());\n");
		text.append("		} catch (Exception e) {\n");
		text.append("			logger.error(e);\n");
		text.append("			baseResponse.setStatue(MessageResources.getInstance().getString(\"E001\"));\n");
		text.append("			baseResponse.setMsg(MessageResources.getInstance().getString(\"M001\"));\n");
		text.append("		}\n");
		text.append("		return \"success\";\n");
		text.append("	}\n\n");

		text.append("	public String view() throws Exception {\n");
		text.append("		String pkId = request.getParameter(\"pkId\");\n");
		text.append("		"+lowerModelName+" = "+lowerServiceName+".selectById(pkId);\n");
		text.append("		return \"view\";\n");
		text.append("	}\n\n");

		text.append("	public "+modelName+" get"+modelName+"() {\n");
		text.append("		return "+lowerModelName+";\n");
		text.append("	}\n\n");

		text.append("	public void set"+modelName+"("+modelName+" "+lowerModelName+") {\n");
		text.append("		this."+lowerModelName+" = "+lowerModelName+";\n");
		text.append("	}\n\n");

		text.append("	public BaseBeanRequest getSearchBean() {\n");
		text.append("		return searchBean;\n");
		text.append("	}\n\n");

		text.append("	public void setSearchBean(BaseBeanRequest searchBean) {\n");
		text.append("		this.searchBean = searchBean;\n");
		text.append("	}\n\n");
		text.append("}");

		//String filePath = srcPath + File.separator + packageStr.replace(".", File.separator) + File.separator;
		//String fileName = actionName + ".java";

		//createFile(filePath, fileName, writeable, text.toString());
	}

	public void generatorSqlMap(String tableName, boolean writeable) throws Exception {

		FieldDesc[] allFieldDescs = this.getAllField(tableName);
		FieldDesc[] keys = this.getKeyField(tableName);

		String className = getClassName(tableName);
		String sqlMapper = className + "_mapper";
		String modelName = className + "Model";
		String fullModelName = packagePrefix+"."+MODEL+"."+modelName;

		StringBuffer text = new StringBuffer("");
		text.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		text.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
		text.append("<mapper namespace=\"" + className + "\">\n");
		text.append("	<!-- start autocreate -->\n\n");

		//生成resultMap
		String resultMap = className+"Result";
		text.append("	<resultMap id=\"" + resultMap + "\" type=\"" + fullModelName + "\">\n");
		for (int i = 0; i < allFieldDescs.length; i++) {
			FieldJavaDesc fieldJava = this.getFieldJavaDesc(allFieldDescs[i]);
			String jdbcType = this.getFieldJdbcType(allFieldDescs[i]);
			if (fieldJava.getFieldType().equals("XClob")) {
				text.append("		<result column=\"" + allFieldDescs[i].getFieldName());
				text.append("\" property=\"" + fieldJava.getFieldName());
				text.append("\" jdbcType=\"CLOB\" javaType=\"java.lang.String\" />\n");
			} else {
				text.append("		<result column=\"" + allFieldDescs[i].getFieldName());
				text.append("\" property=\"" + fieldJava.getFieldName());
				text.append("\" jdbcType=\"" + jdbcType + "\" />\n");
			}
		}
		text.append("	</resultMap>\n\n");

		//生成insert
		text.append("	<insert id=\"insert\" parameterType=\""+fullModelName+"\">\n");
		text.append("		INSERT INTO "+tableName+" (");
		for (int i = 0; i < allFieldDescs.length; i++) {
			text.append("\n\t\t\t");
			text.append(allFieldDescs[i].getFieldName());
			if(i<allFieldDescs.length-1){
				text.append(",");

			}
		}
		text.append("\n\t\t) values(\n");
		for (int i = 0; i < allFieldDescs.length; i++) {
			text.append("\t\t\t");
			FieldJavaDesc fieldJava = this.getFieldJavaDesc(allFieldDescs[i]);
			String jdbcType = this.getFieldJdbcType(allFieldDescs[i]);
			if (fieldJava.getFieldType().equals("XClob")) {
				text.append("#{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}");
			} else {
				text.append("#{"+fieldJava.getFieldName()+",jdbcType=" + jdbcType + "}");
			}
			if(i<allFieldDescs.length-1){
				text.append(",\n");
			}
		}
		text.append("\n\t\t)\n");
		text.append("	</insert>\n\n");

		//生成insertBatch
//		if(dbName!=null && dbName!=""){//mysql
//
//		}else{//oracle
			text.append("	<insert id=\"insertBatch\" parameterType=\"List\">\n");
			text.append("		INSERT INTO "+tableName+" (");
			for (int i = 0; i < allFieldDescs.length; i++) {
				text.append("\n\t\t\t");
				text.append(allFieldDescs[i].getFieldName());
				if(i<allFieldDescs.length-1){
					text.append(",");
				}
			}
			text.append("\n\t\t)\n");
			text.append("		<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\"union all\">\n");
			text.append("			( select\n");
			for (int i = 0; i < allFieldDescs.length; i++) {
				FieldJavaDesc fieldJava = this.getFieldJavaDesc(allFieldDescs[i]);
				String jdbcType = this.getFieldJdbcType(allFieldDescs[i]);
				if (fieldJava.getFieldType().equals("XClob")) {
					text.append("				#{item."+fieldJava.getFieldName()+",jdbcType=VARCHAR}");
				} else {
					text.append("				#{item."+fieldJava.getFieldName()+",jdbcType=" + jdbcType + "}");
				}
				if(i<allFieldDescs.length-1){
					text.append(",\n");
				}
			}
			text.append("\n");
			text.append("		 	from dual)\n");
			text.append("		</foreach>\n");
			text.append("	</insert>\n");
			text.append("\n");
//		}

		//生成主键
		String keyWhere = "";
		for (int i = 0; i < keys.length; i++) {
			FieldJavaDesc javaDesc = this.getFieldJavaDesc(keys[i]);
			String jdbcType = this.getFieldJdbcType(keys[i]);
			if (keyWhere.equals("")) {
				keyWhere += keys[i].getFieldName() + " = #{"
						+ javaDesc.getFieldName() + ",jdbcType=" + jdbcType
						+ "}";
			} else {
				keyWhere += " and " + keys[i].getFieldName() + "= #{"
						+ javaDesc.getFieldName() + ",jdbcType=" + jdbcType
						+ "}";
			}
		}

		//生成deleteById
		text.append("	<delete id=\"deleteById\" parameterType=\"String\">\n");
		text.append("		DELETE FROM  "+tableName+" WHERE "+keyWhere+"\n");
		text.append("	</delete>\n\n");

		//生成deletePK
		text.append("	<delete id=\"deletePK\" parameterType=\""+fullModelName+"\">\n");
		text.append("		DELETE FROM "+tableName+"\n");
		text.append("		<where>\n");
		for (int i = 0; i < allFieldDescs.length; i++) {
			FieldJavaDesc fieldJava = this.getFieldJavaDesc(allFieldDescs[i]);
			String jdbcType = this.getFieldJdbcType(allFieldDescs[i]);
			if (fieldJava.getFieldType().equals("XClob")) {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null and "+fieldJava.getFieldName()+" != ''\" >\n");
				if(i>0) {
					text.append("				AND "+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}else {
					text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}
				text.append("			</if>\n");
			}else if (fieldJava.getFieldType().equals("Date")) {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null\" >\n");
				if(i>0) {
					text.append("				AND "+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}else {
					text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}
				text.append("			</if>\n");
			}  else {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null and "+fieldJava.getFieldName()+" != ''\" >\n");
				if(i>0) {
					text.append("				AND "+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=" + jdbcType + "}\n");
				}else {
					text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=" + jdbcType + "}\n");
				}

				text.append("			</if>\n");
			}
		}
		text.append("		</where>\n");
		text.append("	</delete>\n\n");

		//生成deleteBatch
		text.append("	<delete id=\"deleteBatch\" parameterType=\"List\">\n");
		text.append("		DELETE FROM  "+tableName+" WHERE "+keys[0].getFieldName()+" IN\n");
		text.append("		<foreach collection=\"array\" item=\"item\" open=\"(\" separator=\",\" close=\")\">\n");
		text.append("			#{item}\n");
		text.append("		</foreach>\n");
		text.append("	</delete>\n\n");

		//生成updatePK
		text.append("	<update id=\"updatePK\" parameterType=\""+fullModelName+"\">\n");
		text.append("		UPDATE "+tableName+" \n");
		text.append("		<set>\n");
		for (int i = 0; i < allFieldDescs.length; i++) {
			FieldJavaDesc fieldJava = this.getFieldJavaDesc(allFieldDescs[i]);
			String jdbcType = this.getFieldJdbcType(allFieldDescs[i]);
			if (fieldJava.getFieldType().equals("XClob")) {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null and "+fieldJava.getFieldName()+" != ''\" >\n");
				text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR},\n");
				text.append("			</if>\n");
			}else if (fieldJava.getFieldType().equals("Date")) {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null\" >\n");
				text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=" + jdbcType + "},\n");
				text.append("			</if>\n");
			}  else {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null and "+fieldJava.getFieldName()+" != ''\" >\n");
				text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=" + jdbcType + "},\n");
				text.append("			</if>\n");
			}
		}
		text.append("		</set>\n");
		text.append("		WHERE "+keyWhere+"\n");
		text.append("	</update>\n\n");

		//生成selectById
		text.append("	<select id=\"selectById\" parameterType=\"String\" resultMap=\""+resultMap+"\">\n");
		text.append("		SELECT * FROM "+tableName+" WHERE "+keyWhere+"\n");
		text.append("	</select>\n\n");

		//生成selectBy
		text.append("	<select id=\"selectBy\" parameterType=\""+fullModelName+"\" resultMap=\""+resultMap+"\">\n");
		text.append("		SELECT * FROM "+tableName+"\n");
		text.append("		<where>\n");
		for (int i = 0; i < allFieldDescs.length; i++) {
			FieldJavaDesc fieldJava = this.getFieldJavaDesc(allFieldDescs[i]);
			String jdbcType = this.getFieldJdbcType(allFieldDescs[i]);
			if (fieldJava.getFieldType().equals("XClob")) {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null and "+fieldJava.getFieldName()+" != ''\" >\n");
				if(i>0) {
					text.append("				AND "+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}else {
					text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}
				text.append("			</if>\n");
			}else if (fieldJava.getFieldType().equals("Date")) {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null\" >\n");
				if(i>0) {
					text.append("				AND "+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}else {
					text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}
				text.append("			</if>\n");
			}  else {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null and "+fieldJava.getFieldName()+" != ''\" >\n");
				if(i>0) {
					text.append("				AND "+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=" + jdbcType + "}\n");
				}else {
					text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=" + jdbcType + "}\n");
				}

				text.append("			</if>\n");
			}
		}
		text.append("		</where>\n");
		text.append("	</select>\n\n");

		//生成selectListPage
		text.append("	<sql id=\"searchSql\">\n");
		text.append("		SELECT * FROM "+tableName+"\n");
		text.append("		<where>\n");
		text.append("			<include refid=\"searchCondition.searchClause\" />\n");
		text.append("		</where>\n");
		text.append("	</sql>\n\n");

		if(dbName!=null && dbName!=""){//mysql
			text.append("	<select id=\"selectListPage\" resultMap=\""+resultMap+"\" parameterType=\"com.founder.ssm.core.common.SearchCondition\">\n");
			text.append("		<include refid=\"searchSql\" />\n");
			text.append("		limit #{start},#{rows}\n");
			text.append("	</select>\n\n");

		}else{//oracle

			text.append("	<select id=\"selectListPage\" resultMap=\""+resultMap+"\" parameterType=\"com.founder.ssm.core.common.SearchCondition\">\n");
			//text.append("		SELECT * FROM (\n");
			//text.append("			SELECT A.*, ROWNUM RN FROM (\n");
			text.append("				<include refid=\"searchSql\" />\n");
			//text.append("			) A WHERE ROWNUM &lt;= #{end}\n");
			//text.append("		)\n");
			//text.append("		WHERE RN > #{start}\n");
			text.append("	</select>\n\n");

		}

		//生成selectCount
		text.append("	<select id=\"selectCount\" resultType=\"Integer\" parameterType=\"com.founder.ssm.core.common.SearchCondition\">\n");
		text.append("		SELECT COUNT(*) FROM (\n");
		text.append("			<include refid=\"searchSql\" />\n");
		text.append("		) t \n");
		text.append("	</select>\n\n");

		//生成selectCountBy
		text.append("	<select id=\"selectCountBy\" resultType=\"Integer\" parameterType=\""+fullModelName+"\">\n");
		text.append("		SELECT COUNT(*) FROM "+tableName+"\n");
		text.append("		<where>\n");
		for (int i = 0; i < allFieldDescs.length; i++) {
			FieldJavaDesc fieldJava = this.getFieldJavaDesc(allFieldDescs[i]);
			String jdbcType = this.getFieldJdbcType(allFieldDescs[i]);
			if (fieldJava.getFieldType().equals("XClob")) {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null and "+fieldJava.getFieldName()+" != ''\" >\n");
				if(i>0) {
					text.append("				AND "+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}else {
					text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}
				text.append("			</if>\n");
			}else if (fieldJava.getFieldType().equals("Date")) {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null\" >\n");
				if(i>0) {
					text.append("				AND "+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}else {
					text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}
				text.append("			</if>\n");
			}  else {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null and "+fieldJava.getFieldName()+" != ''\" >\n");
				if(i>0) {
					text.append("				AND "+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=" + jdbcType + "}\n");
				}else {
					text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=" + jdbcType + "}\n");
				}

				text.append("			</if>\n");
			}
		}
		text.append("		</where>\n");
		text.append("	</select>\n\n");

		//生成selectList
		text.append("	<select id=\"selectList\" parameterType=\""+fullModelName+"\" resultMap=\""+resultMap+"\">\n");
		text.append("		SELECT * FROM "+tableName+"\n");
		text.append("		<where>\n");
		for (int i = 0; i < allFieldDescs.length; i++) {
			FieldJavaDesc fieldJava = this.getFieldJavaDesc(allFieldDescs[i]);
			String jdbcType = this.getFieldJdbcType(allFieldDescs[i]);
			if (fieldJava.getFieldType().equals("XClob")) {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null and "+fieldJava.getFieldName()+" != ''\" >\n");
				if(i>0) {
					text.append("				AND "+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}else {
					text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}
				text.append("			</if>\n");
			}else if (fieldJava.getFieldType().equals("Date")) {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null\" >\n");
				if(i>0) {
					text.append("				AND "+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}else {
					text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=VARCHAR}\n");
				}
				text.append("			</if>\n");
			}  else {
				text.append("			<if test=\""+fieldJava.getFieldName()+" != null and "+fieldJava.getFieldName()+" != ''\" >\n");
				if(i>0) {
					text.append("				AND "+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=" + jdbcType + "}\n");
				}else {
					text.append("				"+allFieldDescs[i].getFieldName()+" = #{"+fieldJava.getFieldName()+",jdbcType=" + jdbcType + "}\n");
				}

				text.append("			</if>\n");
			}
		}
		text.append("		</where>\n");
		text.append("	</select>\n\n");

		//生成selectSql2Obj
		text.append("	<select id=\"selectSql2Obj\" parameterType=\"java.lang.String\" resultMap=\""+resultMap+"\">\n");
		text.append("		${value}\n");
		text.append("	</select>\n");

		//生成selectSql2Map
		text.append("	<select id=\"selectSql2Map\" parameterType=\"java.lang.String\" resultType=\"Map\">\n");
		text.append("		${value}\n");
		text.append("	</select>\n");
		//生成updateSql
		text.append("	<update id=\"updateSql\" parameterType=\"java.lang.String\">\n");
		text.append("		${value}\n");
		text.append("	</update>\n");
		//生成selectCountSql
		text.append("	<select id=\"selectCountSql\" parameterType=\"java.lang.String\" resultType=\"Integer\">\n");
		text.append("		${value}\n");
		text.append("	</select>\n");

		text.append("	<!-- end autocreate -->\n");
		text.append("</mapper>\n");

		//String filePath = srcPath + File.separator + "mybatis" + File.separator;
		String filePath = workspacePath + File.separator + SqlMapperPath;
		String fileName = sqlMapper + ".xml";

		createFile(filePath, fileName, writeable, text.toString());
	}

	public void generatorJSP_LIST(String tableName, boolean writeable) throws Exception {
		StringBuffer text = new StringBuffer("");

		FieldDesc[] fieldDescs = this.getAllField(tableName);

		String className = getClassName(tableName);
		String moduleName = setFirstLowerCase(className);
		String actionName = className +"Action";
		String lowerActionName = setFirstLowerCase(actionName);

		text.append("<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" pageEncoding=\"UTF-8\"%>\n");
		text.append("<%@taglib uri=\"/struts-tags\" prefix=\"s\" %>\n");
		text.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
		text.append("<html>\n");
		text.append("<head>\n");
		text.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
		text.append("<title></title>\n");
		text.append("</head>\n");
		text.append("<body>\n");
		text.append("	<div style=\"background:#fafafa;margin: 10px;\">\n\n");


		text.append("		<a href=\"#\" id=\""+moduleName+"SearchBtn\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-search'\">查询</a>\n");
		text.append("    </div>\n");
		text.append("    <div style=\"width:100%;height: 6%;\">\n");
		text.append("    	<s:iterator var=\"button\" value=\"#attr.buttons\">\n");
		text.append("    		<a href=\"#\" onclick=\"<s:property value=\"#button.jsFunction\"/>\" class=\"easyui-linkbutton\" data-options=\"iconCls:'<s:property value=\"#button.menuIcon\"/>'\"><s:property value=\"#button.menuName\"/></a>\n");
		text.append("		</s:iterator>\n");
		text.append("    </div>\n\n");
		text.append("	<div style=\"height: 80%;\">\n");
		text.append("		<table id=\""+moduleName+"List\"></table>\n");
		text.append("	</div>\n\n");
		text.append("	<div id=\""+moduleName+"Win\"></div>\n\n");
		text.append("	<script type=\"text/javascript\">\n");
		text.append("	$(function(){\n");
		text.append("			$('#"+moduleName+"List').datagrid({\n");
		text.append("		    	pagination:true,\n");
		text.append("		    	fitColumns:true,\n");
		text.append("		    	fit:true,\n");
		text.append("		        url:'<%=request.getContextPath() %>/"+lowerActionName+"!list',\n");
		text.append("		        columns:[[\n");
		text.append("		            {field:'ck',checkbox:true},\n");
		for (int i = 0; i < fieldDescs.length; i++) {
			FieldJavaDesc fieldJavaDesc = getFieldJavaDesc(fieldDescs[i]);
			//不生成createBy,createDt,updateBy,updateDt
			if("createBy,createDt,updateBy,updateDt".indexOf(fieldJavaDesc.getFieldName())>-1){
				continue;
			}
			text.append("		            {field:'"+ fieldJavaDesc.getFieldName() + "',title:'"+fieldJavaDesc.getFieldComment()+"',width:100},\n");
		}
		text.append("		        ]]\n");
		text.append("		    });\n\n");

		text.append("		    $(\"#"+moduleName+"SearchBtn\").click(function(){\n");
		text.append("		    	$('#"+moduleName+"List').datagrid(\"reload\",{\n\n");
		text.append("		    	})\n");
		text.append("		    });\n\n");
		text.append("	})\n\n");

		text.append("	function "+moduleName+"Add() {\n");
		text.append("		$('#"+moduleName+"Win').window({\n");
		text.append("			title:\"新增\",\n");
		text.append("			width:600,\n");
		text.append("		 	height:400,\n");
		text.append("		 	modal:true,\n");
		text.append("		 	href:\"<%=request.getContextPath() %>/"+lowerActionName+"!toAdd\"\n");
		text.append("		});\n");
		text.append("	}\n\n");

		text.append("	function "+moduleName+"Edit(){\n");
		text.append("		var checkeds = $('#"+moduleName+"List').datagrid(\"getChecked\");\n");
		text.append("			$('#"+moduleName+"Win').window({\n");
		text.append("		    title:\"修改\",\n");
		text.append("		 	width:600,\n");
		text.append("		 	height:400,\n");
		text.append("		 	modal:true,\n");
		text.append("		 	href:\"<%=request.getContextPath() %>/"+lowerActionName+"!toUpdate?pkId=\"+checkeds[0]."+moduleName+"Id\n");
		text.append("		});\n");
		text.append("	}\n\n");

		text.append("	function "+moduleName+"Delete(){\n");
		text.append("		$.messager.confirm('删除确认', '确定要删除选中的记录吗?', function(r){\n");
		text.append("			if (r){\n");
		text.append("				var checkeds = $('#"+moduleName+"List').datagrid(\"getChecked\");\n");
		text.append("				var pkIds = [];\n");
		text.append("		    	for(var i=0;i<checkeds.length;i++){\n");
		text.append("		    		pkIds.push(checkeds[i]."+moduleName+"Id);\n");
		text.append("		    	}\n");
		text.append("		    	$.ajax({\n");
		text.append("		 			type: \"post\",\n");
		text.append("		 			url: \"<%=request.getContextPath() %>/"+lowerActionName+"!delete?pkIds=\"+pkIds.join(','),\n");
		text.append("		 			dataType: \"json\",\n");
		text.append("		 			success: function(data){\n");
		text.append("		 				if(data.statue=='0'){\n");
		text.append("		 					$.messager.alert('提示信息','删除成功!','info');\n");
		text.append("							$(\"#"+moduleName+"SearchBtn\").click();\n");
		text.append("		 				}else{\n");
		text.append("		 					$.messager.alert('提示信息',data.msg,'info');\n");
		text.append("		 				}\n");
		text.append("		 			}\n");
		text.append("		 		 });\n");
		text.append("		    }\n");
		text.append("		});\n");
		text.append("	}\n\n");

		text.append("	function "+moduleName+"View(){\n");
		text.append("		var checkeds = $('#"+moduleName+"List').datagrid(\"getChecked\");\n");
		text.append("		$('#"+moduleName+"Win').window({\n");
		text.append("			title:\"查看详情\",\n");
		text.append("		 	width:600,\n");
		text.append("		 	height:400,\n");
		text.append("		 	modal:true,\n");
		text.append("		 	href:\"<%=request.getContextPath() %>/"+lowerActionName+"!view?pkId=\"+checkeds[0]."+moduleName+"Id\n");
		text.append("		 });\n");
		text.append("	}\n\n");

		text.append("</script>\n");
		text.append("</body>\n");
		text.append("</html>\n");

		//String filePath = srcPath + File.separator + "jsp" + File.separator+moduleName + File.separator;
		//String fileName = moduleName+ "List.jsp";

		//createFile(filePath, fileName, writeable, text.toString());
	}

	public void generatorJSP_ADD(String tableName, boolean writeable) throws Exception {
		StringBuffer text = new StringBuffer("");

		FieldDesc[] fieldDescs = this.getAllField(tableName);

		String className = getClassName(tableName);
		String moduleName = setFirstLowerCase(className);
		String modelName = className + "Model";
		String lowerModelName = setFirstLowerCase(modelName);
		String actionName = className +"Action";
		String lowerActionName = setFirstLowerCase(actionName);

		text.append("<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" pageEncoding=\"UTF-8\"%>\n");
		text.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
		text.append("<html>\n");
		text.append("<head>\n");
		text.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
		text.append("<title></title>\n");
		text.append("</head>\n");
		text.append("<body>\n");
		text.append("	<form id=\""+moduleName+"AddForm\">\n");
		text.append("	<table>\n");
		text.append("		<tr>\n");
		text.append("			<td colspan=\"2\">新增</td>\n");
		text.append("		</tr>\n");

		for (int i = 0; i < fieldDescs.length; i++) {
			FieldJavaDesc fieldJavaDesc = getFieldJavaDesc(fieldDescs[i]);
			//不生成createBy,createDt,updateBy,updateDt
			if("createBy,createDt,updateBy,updateDt".indexOf(fieldJavaDesc.getFieldName())>-1){
				continue;
			}
			text.append("		<tr>\n");
			text.append("			<td>"+fieldJavaDesc.getFieldComment()+"</td><td><input type=\"text\" name=\""+lowerModelName+"."+ fieldJavaDesc.getFieldName() + "\" value=\"\"></td>\n");
			text.append("		</tr>\n");

		}

		text.append("		<tr>\n");
		text.append("			<td colspan=\"2\">\n");
		text.append("				<input type=\"button\" id=\""+moduleName+"AddSubmitBtn\" value=\"确认\">\n");
		text.append("				<input type=\"reset\" value=\"重置\">\n");
		text.append("			</td>\n");
		text.append("		</tr>\n");
		text.append("	</table>\n");
		text.append("	</form>\n\n");
		text.append("	<script type=\"text/javascript\">\n");
		text.append("		$(function(){\n");
		text.append("			$(\"#"+moduleName+"AddSubmitBtn\").click(function(){\n");
		text.append("				//jquery.form.js 用法参考 http://malsup.com/jquery/form/#api\n");
		text.append("				$('#"+moduleName+"AddForm').ajaxSubmit({\n");
		text.append("						type: \"post\",\n");
		text.append("						url: \"<%=request.getContextPath() %>/"+lowerActionName+"!add\",\n");
		text.append("						//data:$(\"#"+moduleName+"AddForm\").serialize(),\n");
		text.append("						dataType:  \"json\",   \n");
		text.append("						beforeSubmit:function (formData, jqForm, options) { \n");
		text.append("						    //var queryString = $.param(formData); \n");
		text.append("						    //此处用于做验证，返回false则阻止AJAX提交\n");
		text.append("						    return true; \n");
		text.append("						},\n");
		text.append("						success:function (responseText, statusText, xhr, $form)  { \n");
		text.append("							$.messager.alert('提示信息',responseText.msg,'info');\n");
		text.append("						    $('#"+moduleName+"Win').window(\"close\");\n");
		text.append("						    $(\"#"+moduleName+"SearchBtn\").click();\n");
		text.append("						}\n");
		text.append("					});\n");
		text.append("			});\n");
		text.append("		});\n");
		text.append("	</script>\n");
		text.append("</body>\n");
		text.append("</html>\n");

		//String filePath = srcPath + File.separator + "jsp" + File.separator+moduleName + File.separator;
		//String fileName = moduleName+ "Add.jsp";

		//createFile(filePath, fileName, writeable, text.toString());
	}

	public void generatorJSP_UPDATE(String tableName, boolean writeable) throws Exception {
		StringBuffer text = new StringBuffer("");

		FieldDesc[] fieldDescs = this.getAllField(tableName);

		String className = getClassName(tableName);
		String moduleName = setFirstLowerCase(className);
		String modelName = className + "Model";
		String lowerModelName = setFirstLowerCase(modelName);
		String actionName = className +"Action";
		String lowerActionName = setFirstLowerCase(actionName);

		text.append("<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" pageEncoding=\"UTF-8\"%>\n");
		text.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
		text.append("<html>\n");
		text.append("<head>\n");
		text.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
		text.append("<title></title>\n");
		text.append("</head>\n");
		text.append("<body>\n");
		text.append("	<form id=\""+moduleName+"UpdateForm\">\n");
		text.append("	<table>\n");
		text.append("		<tr>\n");
		text.append("			<td colspan=\"2\">修改</td>\n");
		text.append("		</tr>\n");

		for (int i = 0; i < fieldDescs.length; i++) {
			FieldJavaDesc fieldJavaDesc = getFieldJavaDesc(fieldDescs[i]);
			//不生成createBy,createDt,updateBy,updateDt
			if("createBy,createDt,updateBy,updateDt".indexOf(fieldJavaDesc.getFieldName())>-1){
				continue;
			}
			text.append("		<tr>\n");
			text.append("			<td>"+fieldJavaDesc.getFieldComment()+"</td><td><input type=\"text\" name=\""+lowerModelName+"."+ fieldJavaDesc.getFieldName() + "\" value=\"${"+lowerModelName+"."+ fieldJavaDesc.getFieldName() + " }\"></td>\n");
			text.append("		</tr>\n");
		}

		text.append("		<tr>\n");
		text.append("			<td colspan=\"2\">\n");
		text.append("				<input type=\"button\" id=\""+moduleName+"UpdateSubmitBtn\" value=\"确认\">\n");
		text.append("				<input type=\"reset\" value=\"重置\">\n");
		text.append("			</td>\n");
		text.append("		</tr>\n");
		text.append("	</table>\n");
		text.append("	</form>\n\n");
		text.append("	<script type=\"text/javascript\">\n");
		text.append("		$(function(){\n");
		text.append("			$(\"#"+moduleName+"UpdateSubmitBtn\").click(function(){\n");
		text.append("				//jquery.form.js 用法参考 http://malsup.com/jquery/form/#api\n");
		text.append("				$('#"+moduleName+"UpdateForm').ajaxSubmit({\n");
		text.append("						type: \"post\",\n");
		text.append("						url: \"<%=request.getContextPath() %>/"+lowerActionName+"!update\",\n");
		text.append("						//data:$(\"#"+moduleName+"UpdateForm\").serialize(),\n");
		text.append("						dataType:  \"json\",   \n");
		text.append("						beforeSubmit:function (formData, jqForm, options) { \n");
		text.append("						    //var queryString = $.param(formData); \n");
		text.append("						    //此处用于做验证，返回false则阻止AJAX提交\n");
		text.append("						    return true; \n");
		text.append("						},\n");
		text.append("						success:function (responseText, statusText, xhr, $form)  { \n");
		text.append("							$.messager.alert('提示信息',responseText.msg,'info');\n");
		text.append("						    $('#"+moduleName+"Win').window(\"close\");\n");
		text.append("						    $(\"#"+moduleName+"SearchBtn\").click();\n");
		text.append("						}\n");
		text.append("					});\n");
		text.append("			});\n");
		text.append("		});\n");
		text.append("	</script>\n");
		text.append("</body>\n");
		text.append("</html>\n");

		//String filePath = srcPath + File.separator + "jsp" + File.separator+moduleName + File.separator;
		//String fileName = moduleName+ "Update.jsp";

		//createFile(filePath, fileName, writeable, text.toString());
	}

	public void generatorJSP_VIEW(String tableName, boolean writeable) throws Exception {
		StringBuffer text = new StringBuffer("");

		FieldDesc[] fieldDescs = this.getAllField(tableName);

		String className = getClassName(tableName);
		//String moduleName = setFirstLowerCase(className);
		String modelName = className + "Model";
		String lowerModelName = setFirstLowerCase(modelName);
		//String actionName = className +"Action";

		text.append("<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" pageEncoding=\"UTF-8\"%>\n");
		text.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
		text.append("<html>\n");
		text.append("<head>\n");
		text.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
		text.append("<title></title>\n");
		text.append("</head>\n");
		text.append("<body>\n");
		text.append("	<table>\n");
		text.append("		<tr>\n");
		text.append("			<td colspan=\"2\">查看详情</td>\n");
		text.append("		</tr>\n");

		for (int i = 0; i < fieldDescs.length; i++) {
			FieldJavaDesc fieldJavaDesc = getFieldJavaDesc(fieldDescs[i]);
			//不生成createBy,createDt,updateBy,updateDt
			if("createBy,createDt,updateBy,updateDt".indexOf(fieldJavaDesc.getFieldName())>-1){
				continue;
			}
			text.append("		<tr>\n");
			text.append("			<td>"+fieldJavaDesc.getFieldComment()+"</td><td>${"+lowerModelName+"."+ fieldJavaDesc.getFieldName() + " }</td>\n");
			text.append("		</tr>\n");

		}
		text.append("	</table>\n");
		text.append("</body>\n");
		text.append("</html>\n");

		//String filePath = srcPath + File.separator + "jsp" + File.separator+moduleName + File.separator;
		//String fileName = moduleName+ "View.jsp";

		//createFile(filePath, fileName, writeable, text.toString());
	}

	/**
	 * 获取指定表的数据库字段的属性,适用于ORACLE数据库
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	private FieldDesc[] getAllField(String tableName) throws Exception {
		String sql = "";
		if(dbName!=null && dbName!=""){//mysql
			sql = "select table_name from information_schema.tables where table_schema='"+dbName+"' and table_name=?;";
		}else{//oracle
			sql = "select * from user_tables t where t.table_name=?";
		}

		Connection conn = DBConnection.getConnection();
		PreparedStatement pstmt = conn.prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		pstmt.setString(1, tableName);
		ResultSet rs = pstmt.executeQuery();
		rs.last(); 					//移到最后一行
		int rowCount = rs.getRow();	//得到当前行号，也就是记录数
		//rs.beforeFirst(); 		//如果还要用结果集，就把指针再移到初始化的位置
		if(rowCount==0){
			throw new Exception("表不存在");
		}

		//获取字段名及字段描述,适用于ORACLE数据库
		Map<String,String> map = new HashMap<String,String>();

		if(dbName!=null && dbName!=""){//mysql
			sql= "select column_name, column_comment COMMENTS from information_schema.columns where table_schema ='"+dbName+"' and table_name = ?" ;
		}else{//oracle
			sql= "select COLUMN_NAME,COMMENTS from user_col_comments t where table_name = ?";
		}

		pstmt = conn.prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		pstmt.setString(1, tableName);
		rs = pstmt.executeQuery();
		while(rs.next()){
			map.put(rs.getString("COLUMN_NAME"), rs.getString("COMMENTS"));
		}

		sql= "select * from " + tableName + " where 1=2";
		//pstmt = conn.prepareStatement(sql);
		pstmt = conn.prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		rs = pstmt.executeQuery();
		ResultSetMetaData metaData = rs.getMetaData();
		List<FieldDesc> list = new ArrayList<FieldDesc>();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			FieldDesc fieldDesc = new FieldDesc();
			fieldDesc.setFieldName(metaData.getColumnName(i));
			fieldDesc.setFieldType(metaData.getColumnType(i));
			fieldDesc.setColumnClassName(metaData.getColumnClassName(i));
			fieldDesc.setDataScale(metaData.getScale(i));
			fieldDesc.setNullable(ResultSetMetaData.columnNullable == metaData
					.isNullable(i) ? true : false);
			fieldDesc.setComments(map.get(metaData.getColumnName(i)));
			list.add(fieldDesc);
		}

		DBConnection.close(conn, pstmt, rs);
		return (FieldDesc[]) list.toArray(new FieldDesc[list.size()]);
	}

	private FieldDesc[] getKeyField(String tableName) throws Exception {
		FieldDesc[] fieldDescs = getAllField(tableName);
		Connection conn = DBConnection.getConnection();
		DatabaseMetaData metaData = conn.getMetaData();
		//ResultSet rs = metaData.getPrimaryKeys(null, scheme, tableName);
		ResultSet rs = metaData.getPrimaryKeys(null, null, tableName);
		Map<String, Integer> hm = new HashMap<String, Integer>();
		List<FieldDesc> list1 = new ArrayList<FieldDesc>();
		while (rs.next()) {
			int keySeq = rs.getShort("KEY_SEQ");
			String columnName = rs.getString("COLUMN_NAME");
			hm.put(columnName, new Integer(keySeq));
			list1.add(null);
		}
		DBConnection.close(conn, null, rs);

		for (int j = 0; j < fieldDescs.length; j++) {
			if (hm.get(fieldDescs[j].getFieldName()) != null) {
				int index = ((Integer) hm.get(fieldDescs[j].getFieldName()))
						.intValue();
				list1.add(index, fieldDescs[j]);
			}
		}
		List<FieldDesc> list = new ArrayList<FieldDesc>();
		for (int i = 0; i < list1.size(); i++) {
			if (list1.get(i) != null) {
				list.add(list1.get(i));
			}
		}
		return (FieldDesc[]) list.toArray(new FieldDesc[list.size()]);
	}

	private String getClassName(String tableName) {
		tableName = tableName.replaceFirst(this.tablePrefix, "");
		String className = getFieldJavaName(tableName);
		return setFirstUpperCase(className);
	}

	private String setFirstUpperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	private String setFirstLowerCase(String s) {
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	private String getFieldJavaName(String fieldName) {
		String tmp = fieldName.toLowerCase();
		String tmp1 = "";
		for (int i = 0; i < tmp.length(); i++) {
			if (tmp.charAt(i) == '_') {
				tmp1 += String.valueOf(tmp.charAt(i + 1)).toUpperCase();
				i++;
			} else {
				tmp1 += tmp.charAt(i);
			}
		}
		return tmp1;
	}

	/**
	 * 将数据库字段属性对应成相应的java属性
	 * @param fieldDesc
	 * @return
	 * @throws Exception
	 */
	private FieldJavaDesc getFieldJavaDesc(FieldDesc fieldDesc) throws Exception {
		FieldJavaDesc fieldJavaDesc = new FieldJavaDesc();
		fieldJavaDesc.setFieldName(getFieldJavaName(fieldDesc.getFieldName()));
		fieldJavaDesc.setFieldType(getFieldJavaType(fieldDesc));// 字段类型的转换
		fieldJavaDesc.setFieldComment(fieldDesc.getComments());
		return fieldJavaDesc;
	}

	private String getFieldJdbcType(FieldDesc fieldDesc) throws Exception {
		int fieldType = fieldDesc.getFieldType();
		switch (fieldType) {
		case Types.BIT:
			return "BIT";
		case Types.TINYINT:
			return "TINYINT";
		case Types.SMALLINT:
			return "SMALLINT";
		case Types.INTEGER:
			return "INTEGER";
		case Types.BIGINT:
			return "BIGINT";
		case Types.FLOAT:
			return "FLOAT";
		case Types.REAL:
			return "REAL";
		case Types.DOUBLE:
			return "DOUBLE";
		case Types.NUMERIC:
			return "NUMERIC";
		case Types.DECIMAL:
			return "DECIMAL";
		case Types.CHAR:
			return "CHAR";
		case Types.VARCHAR:
			return "VARCHAR";
		case Types.LONGVARCHAR:
			return "LONGVARCHAR";
		case Types.DATE:
			return "DATE";
		case Types.TIME:
			return "TIME";
		case Types.TIMESTAMP:
			return "TIMESTAMP";
		case Types.BLOB:
			return "BLOB";
		case Types.CLOB:
			return "CLOB";
		default:
			System.out.println("不能处理类型[" + fieldType + "]");
			return "VARCHAR";
		}
	}

	private String getFieldJavaType(FieldDesc fieldDesc) throws Exception {
		int fieldType = fieldDesc.getFieldType();
		switch (fieldType) {
		case Types.BIT:
				return "Boolean";
		case Types.TINYINT:

			return "Boolean";

		case Types.SMALLINT:

				return "Long";

		case Types.INTEGER:

				return "Integer";

		case Types.BIGINT:

				return "Long";

		case Types.FLOAT:
			if (fieldDesc.getDataScale() <= 0) {
				if (fieldDesc.isNullable()) {
					return "Long";
				} else {
					return "long";
				}
			} else {

					return "Double";

			}
		case Types.REAL:
			if (fieldDesc.getDataScale() <= 0) {

					return "Long";

			} else {

					return "Double";

			}
		case Types.DOUBLE:
			if (fieldDesc.getDataScale() <= 0) {

					return "Long";

			} else {

					return "Double";

			}
		case Types.NUMERIC:
			if (fieldDesc.getDataScale() <= 0) {
				if (fieldDesc.getColumnClassName().equals("java.lang.Double")) {

						return "Double";

				} else {

						return "Long";

				}
			} else {

					return "Double";

			}
		case Types.DECIMAL:
			if (fieldDesc.getDataScale() <= 0) {

					return "Long";

			} else {

					return "Double";

			}
		case Types.CHAR:
			return "String";
		case Types.VARCHAR:
			return "String";
		case Types.LONGVARCHAR:
			return "String";
		case Types.DATE:
			return "Date";
		case Types.TIME:
			return "Date";
		case Types.TIMESTAMP:
			return "Date";
		case Types.BLOB:
			return "XBlob";
		case Types.CLOB:
			return "XClob";
		default:
			System.out.println("不能处理类型[" + fieldType + "]");
			return "String";
		}
	}
	
	private void createFile(String filePath, String fileName,boolean writeable, String text) throws Exception{
		
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		fileName = filePath+fileName;
		File file = new File(fileName);
		if (!file.exists() || writeable) {
			OutputStreamWriter out1 = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
			out1.write(text.toString());
			out1.flush();
			out1.close();
		}
		
		//System.out.println("已生成文件："+fileName);
	}

	private StringBuffer generateNotes(StringBuffer text, String type){
		text.append("/** \n");
		text.append(" * 描述 ["+setFirstUpperCase(type) +"] \n");
		text.append(" * @author "+this.getAuthor()+"\n");

		java.util.Date date = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		text.append(" * @date "+ sdf.format(date)+" \n");
		text.append(" */ \n");

		return text;
	}
	
	public String getPackagePrefix() {
		return packagePrefix;
	}

	public void setPackagePrefix(String packagePrefix) {
		this.packagePrefix = packagePrefix;
	}

	public String getWorkspacePath() {
		return workspacePath;
	}

	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}

	public String getServicePath() {
		return servicePath;
	}

	public void setServicePath(String servicePath) {
		this.servicePath = servicePath;
	}

	public String getServiceImplPath() {
		return serviceImplPath;
	}

	public void setServiceImplPath(String serviceImplPath) {
		this.serviceImplPath = serviceImplPath;
	}

	public String getModelPath() {
		return modelPath;
	}

	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

	public String getSqlMapperPath() {
		return SqlMapperPath;
	}

	public void setSqlMapperPath(String sqlMapperPath) {
		SqlMapperPath = sqlMapperPath;
	}

	public String getControllerPath() {
		return controllerPath;
	}

	public void setControllerPath(String controllerPath) {
		this.controllerPath = controllerPath;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
