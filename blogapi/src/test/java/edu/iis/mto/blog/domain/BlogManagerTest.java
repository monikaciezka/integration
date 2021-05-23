package edu.iis.mto.blog.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.iis.mto.blog.domain.errors.DomainError;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.LikePost;
import edu.iis.mto.blog.domain.repository.BlogPostRepository;
import edu.iis.mto.blog.domain.repository.LikePostRepository;
import edu.iis.mto.blog.mapper.BlogDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import edu.iis.mto.blog.api.request.UserRequest;
import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.User;
import edu.iis.mto.blog.domain.repository.UserRepository;
import edu.iis.mto.blog.services.BlogService;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class BlogManagerTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BlogPostRepository blogPostRepository;

    @MockBean
    private LikePostRepository likePostRepository;


    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogDataMapper mapper;

    private BlogManager blogManager;

    @Captor
    private ArgumentCaptor<User> userParam;

    @Captor
    private ArgumentCaptor<LikePost> likePostParam;

    private User user;
    private BlogPost blogPost;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setEmail("johnny@domain.com");
        user.setId(5L);
        blogPost = new BlogPost();
        blogPost.setEntry("good day");
        User author = new User();
        author.setId(1L);
        blogPost.setUser(author);
        blogManager = new BlogManager(userRepository, blogPostRepository, likePostRepository, mapper);

    }

    @Test
    void creatingNewUserShouldSetAccountStatusToNEW() {
        blogService.createUser(new UserRequest("John", "Steward", "john@domain.com"));
        verify(userRepository).save(userParam.capture());
        User user = userParam.getValue();
        assertThat(user.getAccountStatus(), equalTo(AccountStatus.NEW));
    }

    @Test
    void shouldAllowLikePostWhenUserIsConfirmed() {
        user.setAccountStatus(AccountStatus.CONFIRMED);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(blogPostRepository.findById(any())).thenReturn(Optional.of(blogPost));
        when(likePostRepository.findByUserAndPost(any(), any())).thenReturn(Optional.empty());

        Boolean result = blogManager.addLikeToPost(user.getId(), blogPost.getId());

        verify(likePostRepository).save(likePostParam.capture());
        assertThat(likePostParam.getValue().getUser(), equalTo(user));
        assertThat(likePostParam.getValue().getPost(), equalTo(blogPost));
        assertThat(result, equalTo(true));
    }

    @Test
    void shouldThrowDomainErrorWhenNotConfirmedUserTriesToLikePost(){
        user.setAccountStatus(AccountStatus.NEW);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        assertThrows(DomainError.class, ()-> {
        blogManager.addLikeToPost(user.getId(), blogPost.getId());});
    }
}
