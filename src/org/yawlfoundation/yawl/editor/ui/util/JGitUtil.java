package org.yawlfoundation.yawl.editor.ui.util;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.swing.YStatusBar;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class JGitUtil{

	public static String localRepoPath = "D:/repo";
    public static String remoteRepoURI = "https://github.com/hhluci/yawl.git"; 
    private static Logger logger = LoggerFactory.getLogger(JGitUtil.class);
    
    public  void clone(String localRepoPath, String remoteRepoURI) {
    	try {
    		File localPath = new File(localRepoPath);
			logger.debug("Cloning from " + remoteRepoURI + " to " + localPath);
		 
			
			//YAWLEditor yawlInstance = YAWLEditor.getInstance();
			//yawlInstance.setCursor(Cursor.CURSOR_WAIT);
		       Git result = Git.cloneRepository()
		                .setURI(remoteRepoURI)
		                .setDirectory(localPath)
		                .setProgressMonitor(new SimpleProgressMonitor())
		                .call();
			       logger.debug("Having repository: " + result.getRepository().getDirectory());
			
		
              	 /* YStatusBar status = YAWLEditor.getStatusBar();
              	status.setText("completed cloning. Having repository:" + result.getRepository().getDirectory());*/
               
           
			//yawlInstance.setCursor(Cursor.CURSOR_DEFAULT);
		       result.close();
				
		}catch (InvalidRemoteException e) {
			logger.error(e.getMessage());
			//e.printStackTrace();
		} catch (TransportException e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (GitAPIException e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
		}
    public static void log(String localRepoPath) throws IOException, NoHeadException, GitAPIException {
    	Git git = Git.open(new File("D:\\repo\\.git"));
    	 Iterable<RevCommit> iterable=git.log().addPath("readme").setMaxCount(5).call();
         Iterator<RevCommit> iter=iterable.iterator();
         while (iter.hasNext()){
             RevCommit commit=iter.next();
             String email=commit.getAuthorIdent().getEmailAddress();
             String name=commit.getAuthorIdent().getName();  //作者

             String commitEmail=commit.getCommitterIdent().getEmailAddress();//提交者
             String commitName=commit.getCommitterIdent().getName();

             int time=commit.getCommitTime();

             String fullMessage=commit.getFullMessage();
             String shortMessage=commit.getShortMessage();  //返回message的firstLine

             String commitID=commit.getName();  //这个应该就是提交的版本号

             logger.debug("authorEmail:"+email);
             logger.debug("authorName:"+name);
             logger.debug("commitEmail:"+commitEmail);
             logger.debug("commitName:"+commitName);
             logger.debug("time:"+time);
             logger.debug("fullMessage:"+fullMessage);
             logger.debug("shortMessage:"+shortMessage);
             logger.debug("commitID:"+commitID);
         }

    }
    public static void commit(String localRepoPath) {
    		
			try {
				File localPath = new File(localRepoPath);   
	    		Git git = Git.open(localPath);
	    		Status status = git.status().call();
	            logger.debug("Modified: " + status.getModified());
	            AddCommand add = git.add();
	            add.addFilepattern(".");
	            add.call();
	            CommitCommand commits = git.commit();
	            commits.setAuthor("hhluci", "hhluci@163.com");
	            commits.setOnly("readme");
	            commits.setMessage("update readme");
	            commits.call();
	            git.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error(e.getMessage());
			} catch (NoHeadException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error(e.getMessage());
			} catch (NoMessageException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error(e.getMessage());
			} catch (UnmergedPathsException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error(e.getMessage());
			} catch (ConcurrentRefUpdateException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error(e.getMessage());
			} catch (WrongRepositoryStateException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error(e.getMessage());
			} catch (AbortedByHookException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error(e.getMessage());
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error(e.getMessage());
			} 

    }
    public static void push(String localRepoPath) {
    	try {
			File localPath = new File(localRepoPath);   
    		Git git = Git.open(localPath);
    		Status status = git.status().call();
            logger.debug("Modified: " + status.getModified());
           
            PushCommand push = git.push();
            push.setCredentialsProvider(new UsernamePasswordCredentialsProvider("hhluci", "Trudy258369"));
            
            Iterable<PushResult> iterable =push.call();
            
            
            PushResult pushResult = iterable.iterator().next();
            RemoteRefUpdate.Status statu = pushResult.getRemoteUpdate( "refs/heads/master" ).getStatus();
            logger.debug(statu.toString());
            git.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (NoMessageException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (UnmergedPathsException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (ConcurrentRefUpdateException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (WrongRepositoryStateException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (AbortedByHookException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} 
    }
    public static void pull(String localRepoPath) {
    	/*FetchResult fetchResult = local.fetch().call();
    	TrackingRefUpdate refUpdate
    	  = fetchResult.getTrackingRefUpdate( "refs/remotes/origin/master" );
    	Result result = refUpdate.getResult();*/
    	try {
			File localPath = new File(localRepoPath);   
    		Git git = Git.open(localPath);
    		/*Status status = git.status().call();
            logger.debug("Modified: " + status.getModified());*/
           
            PullCommand pullCmd = git.pull();
            PullResult result = pullCmd.call();
            FetchResult fetchResult = result.getFetchResult();
            MergeResult mergeResult = result.getMergeResult();
            mergeResult.getMergeStatus();  // this should be interesting
        	/*TrackingRefUpdate refUpdate
        	  = fetchResult.getTrackingRefUpdate( "refs/remotes/origin/master" );
        	Result result = refUpdate.getResult();
            logger.debug(result.toString());*/
            logger.debug(mergeResult.getMergeStatus().toString());
            git.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (NoMessageException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (UnmergedPathsException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (ConcurrentRefUpdateException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (WrongRepositoryStateException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (AbortedByHookException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		} 
    }
    public static String pushToNewBrach(String branchName) {
    	String newBranchIndex = "refs/heads/"+branchName;
        String gitPathURI = "";
   
        try {
            File localPath = new File(localRepoPath); 
            Git git=Git.open( localPath);
            
            //检查新建的分支是否已经存在，如果存在则将已存在的分支强制删除并新建一个分支
            List<Ref> refs = git.branchList().call();
            for (Ref ref : refs) {
                if (ref.getName().equals(newBranchIndex)) {
                    System.out.println("Removing branch before");
                    git.branchDelete().setBranchNames(branchName).setForce(true)
                            .call();
                    break;
                }
            }            
            //新建分支
            Ref ref = git.branchCreate().setName(branchName).call();
            //推送到远程
            git.push().add(ref).setCredentialsProvider(new UsernamePasswordCredentialsProvider("hhluci", "Trudy258369")).call();
            gitPathURI = remoteRepoURI + " " + "feature/" + branchName;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return gitPathURI;     
    }
    private static class SimpleProgressMonitor implements ProgressMonitor {
    	private static YStatusBar status = YAWLEditor.getStatusBar();
		
        @Override
        public void start(int totalTasks) {
            logger.debug("Starting work on " + totalTasks + " tasks");
            status.setText("Starting work on " + totalTasks + " tasks");
        }

        @Override
        public void beginTask(String title, int totalWork) {
        	logger.debug("Start " + title + ": " + totalWork);
        	status.setText("Start " + title + ": " + totalWork);
        }

        @Override
        public void update(int completed) {
        	logger.debug(completed + "-");
        	status.setText(completed + "-");
        }

        @Override
        public void endTask() {
        	logger.debug("Done");
        	status.setText("Done");
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }
    public static void main(String[]args) {
    	//clone(localRepoPath,remoteRepoURI);
    	//commit(localRepoPath);
    	//push(localRepoPath);
    	//pull(localRepoPath);
    	logger.debug(pushToNewBrach("new01"));
    }

    
}
