import React, { useState } from 'react';
import { makeStyles } from '@mui/styles';
import Typography from '@mui/material/Typography';
import { useDispatch } from 'react-redux';
import { useParams } from 'react-router-dom';
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Grid from '@mui/material/Grid';
import Avatar from '@mui/material/Avatar';
import * as R from 'ramda';
import Tooltip from '@mui/material/Tooltip';
import Chip from '@mui/material/Chip';
import Button from '@mui/material/Button';
import {
  ChatBubbleOutlineOutlined,
  ShareOutlined,
  FavoriteBorderOutlined,
} from '@mui/icons-material';
import DefinitionMenu from '../DefinitionMenu';
import { isExerciseUpdatable } from '../../../../utils/Exercise';
import { useHelper } from '../../../../store';
import CreateArticle from './CreateArticle';
import useDataLoader from '../../../../utils/ServerSideEvent';
import { fetchExerciseArticles, fetchMedias } from '../../../../actions/Media';
import useSearchAnFilter from '../../../../utils/SortingFiltering';
import SearchFilter from '../../../../components/SearchFilter';
import { useFormatter } from '../../../../components/i18n';
import MediasFilter from '../../medias/MediasFilter';
import { fetchDocuments } from '../../../../actions/Document';
import ArticlePopover from './ArticlePopover';
import MediaIcon from '../../medias/MediaIcon';
import ExpandableMarkdown from '../../../../components/ExpandableMarkdown';

const useStyles = makeStyles(() => ({
  container: {
    margin: '10px 0 50px 0',
    padding: '0 200px 0 0',
  },
  media: {
    fontSize: 12,
    float: 'left',
    marginRight: 7,
    maxWidth: 300,
  },
  footer: {
    margin: '10px 0 0 0',
    padding: '0 0 20px 0',
  },
}));

const Articles = () => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const { t } = useFormatter();
  const [medias, setMedias] = useState([]);
  const handleAddMedia = (value) => {
    setMedias(R.uniq(R.append(value, medias)));
  };
  const handleRemoveMedia = (value) => {
    const remainingTags = R.filter((n) => n.id !== value, medias);
    setMedias(remainingTags);
  };
  // Fetching data
  const { exerciseId } = useParams();
  const { exercise, articles, mediasMap, documentsMap } = useHelper(
    (helper) => ({
      exercise: helper.getExercise(exerciseId),
      mediasMap: helper.getMediasMap(),
      documentsMap: helper.getDocumentsMap(),
      articles: helper.getExerciseArticles(exerciseId),
    }),
  );
  useDataLoader(() => {
    dispatch(fetchExerciseArticles(exerciseId));
    dispatch(fetchMedias());
    dispatch(fetchDocuments());
  });
  // Filter and sort hook
  const searchColumns = ['name', 'type', 'content'];
  const filtering = useSearchAnFilter('article', 'name', searchColumns);
  // Rendering
  const fullArticles = articles.map((item) => ({
    ...item,
    article_fullmedia: mediasMap[item.article_media] || {},
  }));
  const sortedArticles = R.filter(
    (n) => medias.length === 0
      || medias.map((o) => o.id).includes(n.article_fullmedia.media_id),
    filtering.filterAndSort(fullArticles),
  );
  const mediaColor = (type) => {
    switch (type) {
      case 'newspaper':
        return '#3f51b5';
      case 'microblogging':
        return '#00bcd4';
      case 'tv':
        return '#ff9800';
      default:
        return '#ef41e1';
    }
  };
  return (
    <div className={classes.container}>
      <DefinitionMenu exerciseId={exerciseId} />
      <div>
        <div style={{ float: 'left', marginRight: 20 }}>
          <SearchFilter
            small={true}
            onChange={filtering.handleSearch}
            keyword={filtering.keyword}
          />
        </div>
        <div style={{ float: 'left', marginRight: 20 }}>
          <MediasFilter
            onAddMedia={handleAddMedia}
            onRemoveMedia={handleRemoveMedia}
            currentMedias={medias}
          />
        </div>
      </div>
      <div className="clearfix" />
      <Grid container={true} spacing={3}>
        {sortedArticles.map((article) => {
          const docs = article.article_documents
            .map((d) => (documentsMap[d] ? documentsMap[d] : undefined))
            .filter((d) => d !== undefined);
          let columns = 12;
          if (docs.length === 2) {
            columns = 6;
          } else if (docs.length === 3) {
            columns = 4;
          } else {
            columns = 3;
          }
          // const shouldBeTruncated = (article.article_content || '').length > 500;
          return (
            <Grid item={true} xs={4}>
              <Card
                sx={{ width: '100%', height: '100%' }}
                key={article.article_id}
              >
                <CardHeader
                  avatar={
                    <Avatar
                      sx={{
                        bgcolor: mediaColor(
                          article.article_fullmedia.media_type,
                        ),
                      }}
                    >
                      {(article.article_author || t('Unknown')).charAt(0)}
                    </Avatar>
                  }
                  title={article.article_author || t('Unknown')}
                  subheader={
                    article.article_is_scheduled
                      ? t('Scheduled / in use')
                      : t('Not used in the exercise')
                  }
                  action={
                    <ArticlePopover exercise={exercise} article={article} />
                  }
                />
                <Grid container={true} spacing={3}>
                  {docs.map((doc) => (
                    <Grid item={true} xs={columns}>
                      <CardMedia
                        component="img"
                        height="150"
                        image={`/api/documents/${doc.document_id}/file`}
                      />
                    </Grid>
                  ))}
                </Grid>
                <CardContent>
                  <Typography
                    gutterBottom
                    variant="h1"
                    component="div"
                    style={{ margin: '0 auto', textAlign: 'center' }}
                  >
                    {article.article_name}
                  </Typography>
                  <ExpandableMarkdown
                    source={article.article_content}
                    limit={500}
                    controlled={true}
                  />
                  <div className={classes.footer}>
                    <div style={{ float: 'left' }}>
                      <Tooltip title={article.article_fullmedia.media_name}>
                        <Chip
                          icon={
                            <MediaIcon
                              type={article.article_fullmedia.media_type}
                              variant="chip"
                            />
                          }
                          classes={{ root: classes.media }}
                          style={{
                            color: mediaColor(
                              article.article_fullmedia.media_type,
                            ),
                            borderColor: mediaColor(
                              article.article_fullmedia.media_type,
                            ),
                          }}
                          variant="outlined"
                          label={article.article_fullmedia.media_name}
                        />
                      </Tooltip>
                    </div>
                    <div style={{ float: 'right' }}>
                      <Button
                        size="small"
                        startIcon={<ChatBubbleOutlineOutlined />}
                      >
                        {article.article_comments || 0}
                      </Button>
                      <Button size="small" startIcon={<ShareOutlined />}>
                        {article.article_shares || 0}
                      </Button>
                      <Button
                        size="small"
                        startIcon={<FavoriteBorderOutlined />}
                      >
                        {article.article_likes || 0}
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </Grid>
          );
        })}
      </Grid>
      {isExerciseUpdatable(exercise) && (
        <CreateArticle exerciseId={exercise.exercise_id} />
      )}
    </div>
  );
};

export default Articles;
