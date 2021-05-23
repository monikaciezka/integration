package edu.iis.mto.blog.domain.repository;

import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.LikePost;
import edu.iis.mto.blog.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class LikePostRepositoryTest {

    @Autowired
    LikePostRepository likePostRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BlogPostRepository blogPostRepository;
    User user;
    BlogPost blogPost;
    LikePost likePost;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Jan");
        user.setEmail("john@domain.com");
        user.setAccountStatus(AccountStatus.NEW);
        userRepository.save(user);

        blogPost = new BlogPost();
        blogPost.setUser(user);
        blogPost.setEntry("first entry");
        blogPostRepository.save(blogPost);

        likePost = new LikePost();
        likePost.setPost(blogPost);
        likePost.setUser(user);

    }

    @Test
    void shouldFindLikePost() {
        likePostRepository.save(likePost);
        Optional<LikePost> likePost = likePostRepository.findByUserAndPost(user, blogPost);
        if(likePost.isPresent()){
           assertThat(true, equalTo(likePost.get().getPost().equals(blogPost)));
        } else {
            fail("object not found");
        }
    }

    @Test
    void shouldNotFindLikePostWhenRepositoryEmpty(){
        Optional<LikePost> likePosts = likePostRepository.findByUserAndPost(user, blogPost);
        assertThat(false, equalTo(likePosts.isPresent()));
    }

    @Test
    void shouldSaveLikePost() {
        likePostRepository.save(likePost);
        assertThat(1, equalTo(likePostRepository.findAll().size()));
    }

    @Test
    void shouldNotFindLikePostWhenIncorrectData(){
        User newUser = new User();
        newUser.setFirstName("John");
        newUser.setEmail("john1@domain.com");
        newUser.setAccountStatus(AccountStatus.CONFIRMED);
        userRepository.save(newUser);
        BlogPost newPost = new BlogPost();
        newPost.setEntry("new post");
        newPost.setUser(newUser);
        blogPostRepository.save(newPost);
        Optional<LikePost> likePosts = likePostRepository.findByUserAndPost(newUser, newPost);
        assertThat(false, equalTo(likePosts.isPresent()));
    }
}
