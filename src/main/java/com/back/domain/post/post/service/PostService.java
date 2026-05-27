package com.back.domain.post.post.service;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.postComment.entity.PostComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public long count() {
        return postRepository.count();
    }

    public Post write(String title, String content, String author) {
        Post post = new Post(title, content, author);

        return postRepository.save(post);
    }

    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public void modify(Post post, String title, String content, String author) {
        post.modify(title, content, author);
    }

    public PostComment writeComment(Post post, String content, String author) {
        return post.addComment(content, author);
    }

    public boolean deleteComment(Post post, PostComment postComment, String author) {
        return post.deleteComment(postComment, author);
    }

    public void modifyComment(PostComment postComment, String content, String author) {
        postComment.modify(content, author);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public Optional<Post> findLatest() {
        return postRepository.findFirstByOrderByIdDesc();
    }

    // 영속성 컨텍스트의 변경 내용을 DB에 즉시 반영하는 역할
    // 트랜잭션이 끝나기 전이라도, 지금까지 메모리에 쌓여있던 쿼리를 즉시 DB로 전송
    public void flush(){
        postRepository.flush();
    }
}